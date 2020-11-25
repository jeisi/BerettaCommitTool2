/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNoMessageException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNothingAddedException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitUnmergedFilesException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitRevertCommand extends BaseSingleGitCommand {

    public GitRevertCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void revert(String option) throws GitConfigException, IOException, InterruptedException {
        List<String> command = getRevertCommand(option);
        List<String> displayCommand = getRevertCommand(option);
        execProcess(command, displayCommand);
    }

    private List<String> getRevertCommand(String option) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("revert");
        command.add(option);
        return command;
    }
}
