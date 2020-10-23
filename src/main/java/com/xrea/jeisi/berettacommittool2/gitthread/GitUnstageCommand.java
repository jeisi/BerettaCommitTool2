/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitUnstageCommand extends BaseMultiGitCommand {

    //private final File repository;
    //private ProgressWindow progressWindow;
    //private ProgressModel progressModel = null;
    public GitUnstageCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void unstage(List<GitStatusData> datas) throws IOException, GitAPIException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> execProcess("git", "reset", "HEAD", data.getFileName()));
        /*
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
         */
    }

    public void unstage(GitStatusData data) throws IOException, GitAPIException, GitConfigException, InterruptedException {
        unstage(Arrays.asList(data));
    }
    
    /*
    protected void unstageFile(Git git, String file) throws GitAPIException {
        git.reset().addPath(file).setRef("HEAD").call();
    }

    private void removeCache(Git git, String file) throws GitAPIException {
        git.rm().setCached(true).addFilepattern(file).call();
    }

    protected Git gitOpen() throws IOException {
        return Git.open(repository);
    }
     */
}
