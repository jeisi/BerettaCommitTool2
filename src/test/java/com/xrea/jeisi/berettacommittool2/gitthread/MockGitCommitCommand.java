/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class MockGitCommitCommand extends GitCommitCommand {

    private GitAPIException exception;

    public MockGitCommitCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void setException(GitAPIException exception) {
        this.exception = exception;
    }

    public void commit(String message, boolean amend) throws IOException, GitAPIException {
        if(exception != null) {
            throw exception;
        }
    }
}
