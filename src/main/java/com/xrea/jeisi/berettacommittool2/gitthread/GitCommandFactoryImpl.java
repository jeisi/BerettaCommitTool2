/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author jeisi
 */
public class GitCommandFactoryImpl implements GitCommandFactory {

    @Override
    public GitStatusCommand createStatusCommand(Path file, ConfigInfo configInfo) {
        return new GitStatusCommand(file, configInfo);
    }

    @Override
    public GitAddCommand createAddCommand(Path path, ConfigInfo configInfo) {
        return new GitAddCommand(path, configInfo);
    }
    
    @Override
    public GitUnstageCommand createUnstageCommand(File file) {
        return new GitUnstageCommand(file);
    }
    
    @Override
    public GitCommitCommand createGitCommitCommand(File file) {
        return new GitCommitCommand(file);
    }
    
    @Override
    public GitDiffCommand createGitDiffCommand(Path path, ConfigInfo configInfo) {
        return new GitDiffCommand(path, configInfo);
    }
}
