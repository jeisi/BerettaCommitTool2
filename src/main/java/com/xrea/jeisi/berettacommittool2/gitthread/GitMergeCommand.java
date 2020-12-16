/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitMergeCommand extends BaseSingleGitCommand {

    public GitMergeCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void merge(String option) throws GitConfigException, IOException, InterruptedException {
        List<String> command = getMergeCommand(option);
        List<String> displayCommand = getMergeCommand(option);
        execProcess(command, displayCommand);
    }

    private List<String> getMergeCommand(String option) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("merge");
        command.add(option);
        return command;
    }
}
