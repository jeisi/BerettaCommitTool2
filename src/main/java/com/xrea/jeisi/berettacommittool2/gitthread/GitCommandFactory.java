/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import java.io.File;

/**
 *
 * @author jeisi
 */
public interface GitCommandFactory {
    public GitAddCommand createAddCommand(File file);
    public GitStatusCommand createStatusCommand(File file);
    public GitUnstageCommand createUnstageCommand(File file);
}
