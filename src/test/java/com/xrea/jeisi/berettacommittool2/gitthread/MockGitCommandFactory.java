/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jeisi
 */
public class MockGitCommandFactory implements GitCommandFactory {

    private Map<File, MockGitStatusCommand> gitStatusCommands = new HashMap<>();
    private Map<File, MockGitAddCommand> gitAddCommands = new HashMap<>();
    private Map<File, MockGitUnstageCommand> gitUnstageCommands = new HashMap<>();

    public void setMockGitStatusCommand(File file, MockGitStatusCommand gitCommand) {
        //System.out.println("MockGitCommandFactory.setMockGitStatusCommand(): file = " + file.toString());
        this.gitStatusCommands.put(file, gitCommand);
    }

    public void setMockGitAddCommand(File file, MockGitAddCommand gitCommand) {
        this.gitAddCommands.put(file, gitCommand);
    }

    @Override
    public GitStatusCommand createStatusCommand(File file) {
        //System.out.println("MockGitCommandFactory.createStatusCommand(): file = " + file.toString());
        GitStatusCommand command = gitStatusCommands.get(file);
        if (command == null) {
            StringBuilder builder = new StringBuilder();
            //builder.append(String.format("Error!! GitStatusCommand.createStatusCommand(%s)\n", file.toString()));
            //builder.append("gitStatusCommands: [");
            System.out.println(String.format("Error!! GitCommand.create(%s)\n", file.toString()));
            System.out.println("gitStatusCommands: " + gitStatusCommands.keySet().toString());
        }
        return command;
    }

    @Override
    public GitAddCommand createAddCommand(File file) {
        GitAddCommand command = gitAddCommands.get(file);
        if (command == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("Error!! MockGitCommandFactory.createAddCommand(");
            builder.append(file.toString());
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
}
