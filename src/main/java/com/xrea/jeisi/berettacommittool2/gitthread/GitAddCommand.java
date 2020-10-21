/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;

/**
 *
 * @author jeisi
 */
public class GitAddCommand extends BaseGitCommand {

    public GitAddCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void add(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> execProcess("git", "add", data.getFileName()));
    }

    public void add(GitStatusData data) throws IOException, GitConfigException, InterruptedException {
        add(Arrays.asList(data));
    }

    // git add -u
    public void addUpdate() throws GitConfigException, IOException, InterruptedException {
        addFilesByOption("-u");
    }

    // git add -A
    public void addAll() throws GitConfigException, IOException, InterruptedException {
        addFilesByOption("-A");
    }

    private void addFilesByOption(String option) throws GitConfigException, IOException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.addFilesByOption()");

        //List<String> command = getCommand("add", "--dry-run", option);
        List<String> lines = execProcessWithOutput("git", "add", "--dry-run", option);
        if (progressWindow != null && lines.size() > 1) {
            progressModel = new ProgressModel(String.format("git %s ...", lines.get(0)), lines.size());
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        ProcessBuilder pb = new ProcessBuilder(getCommand("git", "add", "-v", option));
        pb.directory(repository);
        Process process = pb.start();
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Pattern p = Pattern.compile("add '.*'");
        String line;
        int currentValue = 0;
        while ((line = reader.readLine()) != null) {
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

    /*
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

    private List<String> getCommand(String... args) throws GitConfigException {
        var git = configInfo.getProgramEx("git");
        ArrayList<String> command = new ArrayList<>();
        command.add(git);
        command.addAll(Arrays.asList(args));
        return command;
    }
     */
}
