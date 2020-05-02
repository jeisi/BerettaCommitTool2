/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitUnstageCommand {

    private final File repository;
    private ProgressWindow progressWindow;
    private ProgressModel progressModel = null;

    public GitUnstageCommand(File repository) {
        this.repository = repository;
    }

    public void setProgressWindow(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

    public void unstage(String... files) throws IOException, GitAPIException {
        if (progressWindow != null && files.length > 1) {
            Platform.runLater(() -> {
                progressWindow.open();
                progressModel = new ProgressModel(String.format("git reset HEAD %s ...", files[0]), files.length);
                progressWindow.addProgressModel(progressModel);
            });
        }

        Git git = gitOpen();
        boolean existBranch = git.branchList().call().size() > 0;
        int currentValue = 0;
        for (var file : files) {
            if (existBranch) {
                unstageFile(git, file);
            } else {
                removeCache(git, file);
            }
            ++currentValue;
            if (progressModel != null) {
                progressModel.setCurrentValue(currentValue);
            }
        }
    }

    protected void unstageFile(Git git, String file) throws GitAPIException {
        git.reset().addPath(file).setRef("HEAD").call();
    }

    private void removeCache(Git git, String file) throws GitAPIException {
        git.rm().setCached(true).addFilepattern(file).call();
    }

    protected Git gitOpen() throws IOException {
        return Git.open(repository);
    }
}
