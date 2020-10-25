/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

/**
 *
 * @author jeisi
 */
public class GitCommitNothingAddedException extends GitCommandException {
    public GitCommitNothingAddedException(GitCommandException e) {
        super(e.header, e.stdout, e.stderr);
    }
    
    @Override
    public String getMessage() {
        return header + "nothing added to commit but untracked files present\n";
    }
}
