/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import static com.xrea.jeisi.berettacommittool2.gitthread.BaseMultiGitCommand.getErrorMessage;
import static com.xrea.jeisi.berettacommittool2.gitthread.BaseMultiGitCommand.getInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class BaseSingleGitCommand {

    protected final ConfigInfo configInfo;
    protected final File repository;

    BaseSingleGitCommand(Path repository, ConfigInfo configInfo) {
        this.repository = repository.toFile();
        this.configInfo = configInfo;
    }

    protected void execProcess(List<String> args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        String[] command = args.toArray(new String[args.size()]);
        execProcess(command);
    }

    protected void execProcess(String... args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(args));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }

    protected List<String> execProcessWithOutput(String... args) throws IOException, InterruptedException, GitConfigException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(args));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        return getInputStream(process);
    }

    protected String[] getCommand(String... args) throws GitConfigException {
        String command = configInfo.getProgramEx(args[0]);
        if (command != null) {
            args[0] = command;
        }
        return args;
    }

}
