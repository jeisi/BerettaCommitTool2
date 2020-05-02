/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitCommitCommand {

    private final File repository;

    public GitCommitCommand(File repository) {
        this.repository = repository;
    }
    
    public void commit(String message, boolean amend) throws IOException, GitAPIException {
        Git.open(repository).commit().setAmend(amend).call();
    }
    
    public String readCommitEditMsg() throws IOException {
        return Git.open(repository).getRepository().readCommitEditMsg();
    }
}
