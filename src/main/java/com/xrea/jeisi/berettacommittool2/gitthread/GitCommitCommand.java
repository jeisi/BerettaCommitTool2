/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
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
        System.out.println("GitCommitcommand.commit() start.");
        Git git = Git.open(repository);
        
        Status gitStatus = git.status().call();
        if(gitStatus.getChanged().size() == 0 && gitStatus.getAdded().size() == 0 && gitStatus.getRemoved().size() == 0 &&
                gitStatus.getConflicting().size() == 0) {
            // ステージされていないファイルがなければ何もしない。
            return;
        }
        
        //System.out.println("message: " + message);
        if(message.length() == 0) {
            throw new NoMessageException("Aborting commit due to empty commit message.");
        }
        
        git.commit().setAllowEmpty(false).setMessage(message).setAmend(amend).call();
        System.out.println("GitCommitcommand.commit() end.");
    }
    
    public String readCommitEditMsg() throws IOException {
        return Git.open(repository).getRepository().readCommitEditMsg();
    }
}
