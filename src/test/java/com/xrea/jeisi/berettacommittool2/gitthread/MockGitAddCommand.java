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
public class MockGitAddCommand extends GitAddCommand {

    public MockGitAddCommand(File repoDir) {
        super(repoDir);
    }

    @Override
    protected void addFile(Git git, String file) throws GitAPIException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
           
        }
    }
    
    @Override
    protected Git gitOpen() throws IOException {
        return null;
    }
}
