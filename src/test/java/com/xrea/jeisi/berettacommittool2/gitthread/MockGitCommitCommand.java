/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class MockGitCommitCommand extends GitCommitCommand {

    private GitAPIException exception;

    public MockGitCommitCommand(File repository) {
        super(repository);
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
