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
public interface GitCommandFactory {
    public GitAddCommand createAddCommand(Path path, ConfigInfo configInfo);
    public GitCheckoutCommand createCheckoutCommand(Path path, ConfigInfo configInfo);
    public GitStatusCommand createStatusCommand(Path path, ConfigInfo configInfo);
    public GitUnstageCommand createUnstageCommand(Path path, ConfigInfo configInfo);
    public GitCommitCommand createGitCommitCommand(File file);
    public GitDiffCommand createGitDiffCommand(Path path, ConfigInfo configInfo);
}
