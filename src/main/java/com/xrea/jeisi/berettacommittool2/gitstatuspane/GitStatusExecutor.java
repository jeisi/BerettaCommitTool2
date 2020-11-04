/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jeisi
 */
public interface GitStatusExecutor {
    public List<GitStatusData> exec(GitStatusCommand statusCommand, RepositoryData repository) throws RepositoryNotFoundException, IOException, GitConfigException, InterruptedException;
}
