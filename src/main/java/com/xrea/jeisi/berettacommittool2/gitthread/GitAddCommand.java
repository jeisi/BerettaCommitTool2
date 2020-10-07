/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitAddCommand extends BaseGitCommand {

    private final ConfigInfo configInfo;
    private final File repository;
    private ProgressWindow progressWindow;
    private ProgressModel progressModel = null;

    public GitAddCommand(Path repository, ConfigInfo configInfo) {
        this.repository = repository.toFile();
        this.configInfo = configInfo;
    }

    public void setProgressWindow(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

    public void add(String... files) throws IOException, GitConfigException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.add(%s)", files.toString());

        if (progressWindow != null && files.length > 1) {
            progressModel = new ProgressModel(String.format("git add %s ...", files[0]), files.length);
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        int currentValue = 0;
        for (var file : files) {
            addFile(file);
            ++currentValue;
            if (progressModel != null) {
                Platform.runLater(new SetCurrentValue(progressModel, currentValue));
            }
        }
        
        XmlWriter.writeEndMethod();
    }

    // git add -u
    public void addUpdate() throws GitConfigException, IOException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.addUpdate()");

        addFilesByOption("-u");
        
        XmlWriter.writeEndMethod();
    }
    
    // git add -A
    public void addAll() throws GitConfigException, IOException, InterruptedException {
        addFilesByOption("-A");
    }
    
    private void addFilesByOption(String option) throws GitConfigException, IOException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.addFilesByOption()");
        
        List<String> command = getCommand("add", "--dry-run", option);
        List<String> lines = execCommandWithOutput(command);
        if (progressWindow != null && lines.size() > 1) {
            progressModel = new ProgressModel(String.format("git %s ...", lines.get(0)), lines.size());
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        command = getCommand("add", "-v", option);
        XmlWriter.writeObject("command", command);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(repository);
        Process process = pb.start();
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Pattern p = Pattern.compile("add '.*'");
        String line;
        int currentValue = 0;
        while ((line = reader.readLine()) != null) {
            XmlWriter.writeObject("line", line);
            Matcher m = p.matcher(line);
            if (m.matches()) {
                ++currentValue;
                if (progressModel != null) {
                    Platform.runLater(new SetCurrentValue(progressModel, currentValue));
                }
            }
        }
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        
        XmlWriter.writeEndMethod(); 
    }

    protected void addFile(String file) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        Path path = Paths.get(repository.toString(), file);
        List<String> command = getCommand("add", file);
        execCommand(command);
    }

    private void execCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }

    private List<String> execCommandWithOutput(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        return getInputStream(process);
    }

    private List<String> getCommand(String... args) throws GitConfigException {
        var git = configInfo.getProgramEx("git");
        ArrayList<String> command = new ArrayList<>();
        command.add(git);
        command.addAll(Arrays.asList(args));
        return command;
    }
}
