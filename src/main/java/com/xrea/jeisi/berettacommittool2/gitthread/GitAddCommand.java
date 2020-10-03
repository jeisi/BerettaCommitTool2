/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitAddCommand {

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

    public void add(String... files) throws IOException, GitAPIException {
        XmlWriter.writeStartMethod("GitAddCommand.add()");

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

        XmlWriter.writeEndMethod();
    }

    protected Git gitOpen() throws IOException {
        return Git.open(repository);
    }

    protected void addFile(Git git, String file) throws GitAPIException {
        XmlWriter.writeStartMethod("GitAddCommand.addFile(%s)", file);

        Path path = Paths.get(repository.toString(), file);
        if (Files.exists(path)) {
            git.add().addFilepattern(file).call();
        } else {
            git.rm().addFilepattern(file).call();
        }

        XmlWriter.writeEndMethod();
    }
}
