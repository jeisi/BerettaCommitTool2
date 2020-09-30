/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.nio.file.Path;

/**
 *
 * @author jeisi
 */
public class MockGitStatusCommand extends GitStatusCommand {

    public MockGitStatusCommand(Path repoDir, ConfigInfo configInfo) {
        super(repoDir, configInfo);
    }
}
