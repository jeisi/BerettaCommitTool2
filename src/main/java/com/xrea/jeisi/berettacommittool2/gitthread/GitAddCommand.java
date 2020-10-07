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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        if (progressWindow != null && files.length > 1) {
            progressModel = new ProgressModel(String.format("git add %s ...", files[0]), files.length);
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        int currentValue = 0;
        Git git = gitOpen();
        for (var file : files) {
            addFile(git, file);
            ++currentValue;
            if (progressModel != null) {
                class SetCurrentValue implements Runnable {

                    private final int currentValue;

                    SetCurrentValue(int currentValue) {
                        this.currentValue = currentValue;
                    }

                    @Override
                    public void run() {
                        progressModel.setCurrentValue(currentValue);
                    }
                }
                //Platform.runLater(() -> progressModel.setCurrentValue(currentValue));
                Platform.runLater(new SetCurrentValue(currentValue));
            }
        }
    }

    protected Git gitOpen() throws IOException {
        return Git.open(repository);
    }

    protected void addFile(Git git, String file) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        Path path = Paths.get(repository.toString(), file);
        List<String> command;
        if (Files.exists(path)) {
            //git.add().addFilepattern(file).call();
            command = getCommand("add", file);
        } else {
            //git.rm().addFilepattern(file).call();
            command = getCommand("rm", file);
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }

    private List<String> getCommand(String gitCommand, String fileName) throws GitConfigException {
        var git = configInfo.getProgramEx("git");
        ArrayList<String> command = new ArrayList<>();
        command.add(git);
        command.add(gitCommand);
        command.add(fileName);
        return command;
    }
}
