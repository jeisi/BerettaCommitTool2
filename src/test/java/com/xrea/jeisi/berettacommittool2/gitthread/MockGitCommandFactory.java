/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jeisi
 */
public class MockGitCommandFactory implements GitCommandFactory {

    private final Map<Path, MockGitStatusCommand> gitStatusCommands = new HashMap<>();
    private final Map<File, MockGitAddCommand> gitAddCommands = new HashMap<>();
    private Map<File, MockGitUnstageCommand> gitUnstageCommands = new HashMap<>();
    private final Map<File, MockGitCommitCommand> gitCommitCommands = new HashMap<>();

    public void setMockGitStatusCommand(Path file, MockGitStatusCommand gitCommand) {
        //System.out.println("MockGitCommandFactory.setMockGitStatusCommand(): file = " + file.toString());
        this.gitStatusCommands.put(file, gitCommand);
    }

    public void setMockGitAddCommand(File file, MockGitAddCommand gitCommand) {
        this.gitAddCommands.put(file, gitCommand);
    }

    public void setMockGitCommitCommand(File file, MockGitCommitCommand gitCommand) {
        this.gitCommitCommands.put(file, gitCommand);
    }
    
    @Override
    public GitStatusCommand createStatusCommand(Path file, ConfigInfo configInfo) {
        GitStatusCommand command = gitStatusCommands.get(file);
        if (command == null) {
            StringBuilder builder = new StringBuilder();
        }
        return command;
    }

    @Override
    public GitAddCommand createAddCommand(Path path, ConfigInfo configInfo) {
        GitAddCommand command = gitAddCommands.get(path);
        if (command == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("Error!! MockGitCommandFactory.createAddCommand(");
            builder.append(path.toString());
            builder.append(")\n");
            builder.append("gitStatusCommands: ");
            builder.append(gitAddCommands.keySet().toString());
            throw new AssertionError(builder.toString());
        }
        return command;
    }

    @Override
    public GitUnstageCommand createUnstageCommand(File file) {
        GitUnstageCommand command = gitUnstageCommands.get(file);
        return command;
    }

    @Override
    public GitCommitCommand createGitCommitCommand(File file) {
        GitCommitCommand command = gitCommitCommands.get(file);
        return command;
    }
    
    @Override
    public GitDiffCommand createGitDiffCommand(Path path, ConfigInfo configInfo) {
        return new GitDiffCommand(path, configInfo);
    }
}
