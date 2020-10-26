/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

import java.nio.file.Path;

/**
 *
 * @author jeisi
 */
public class RepositoryNotFoundException extends GitCommandException {
    
    private final Path repositoryPath;
    
    public RepositoryNotFoundException(Path repositoryPath, GitCommandException e) {
        super(e.header, e.stdout, e.stderr);
        this.repositoryPath = repositoryPath;
    }
    
    public String getShortMessage() {
        return "repository not found: " + repositoryPath.toString();
    }
}
