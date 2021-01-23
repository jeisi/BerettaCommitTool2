/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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

    public void unstage(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> execProcess("git", "reset", "HEAD", data.getFileName()));
    }

    public void unstage(GitStatusData data) throws IOException, GitConfigException, InterruptedException {
        unstage(Arrays.asList(data));
    }
   
}
