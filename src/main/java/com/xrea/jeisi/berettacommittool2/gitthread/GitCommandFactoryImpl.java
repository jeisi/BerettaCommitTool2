/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author jeisi
 */
public class GitCommandFactoryImpl implements GitCommandFactory {

    @Override
    public GitStatusCommand createStatusCommand(File file) {
        return new GitStatusCommand(file);
    }

    @Override
    public GitAddCommand createAddCommand(File file) {
        return new GitAddCommand(file);
    }
    
    @Override
    public GitUnstageCommand createUnstageCommand(File file) {
        return new GitUnstageCommand(file);
    }
    
    @Override
    public GitCommitCommand createGitCommitCommand(File file) {
        return new GitCommitCommand(file);
    }
}
