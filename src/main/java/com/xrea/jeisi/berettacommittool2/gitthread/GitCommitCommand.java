/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;

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
        Git git = Git.open(repository);
        
        Status gitStatus = git.status().call();
        if(gitStatus.getChanged().isEmpty() && gitStatus.getAdded().isEmpty() && gitStatus.getRemoved().isEmpty() &&
                gitStatus.getConflicting().isEmpty()) {
            // ステージされていないファイルがなければ何もしない。
            return;
        }
        
        if(message.length() == 0) {
            throw new NoMessageException("Aborting commit due to empty commit message.");
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            git.commit().setAllowEmpty(false).setMessage(message).setAmend(amend)
                .setHookOutputStream(new PrintStream(outputStream)).call();
        } catch(AbortedByHookException e) {
            throw new AbortedByHookException(outputStream.toString(), e.getHookName(), e.getReturnCode());
        } 
    }
    
    public String readCommitEditMsg() throws IOException {
        return Git.open(repository).getRepository().readCommitEditMsg();
    }
}
