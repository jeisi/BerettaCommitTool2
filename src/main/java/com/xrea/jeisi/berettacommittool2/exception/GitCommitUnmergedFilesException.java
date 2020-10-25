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
public class GitCommitUnmergedFilesException extends GitCommandException {
    public GitCommitUnmergedFilesException(GitCommandException e) {
        super(e.header, e.stdout, e.stderr);
    }
}
