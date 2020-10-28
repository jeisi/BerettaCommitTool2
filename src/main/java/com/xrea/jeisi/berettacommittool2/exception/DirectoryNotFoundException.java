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
public class DirectoryNotFoundException extends GitCommandException {

    private final String directory;

    public DirectoryNotFoundException(String directory, GitCommandException e) {
        super(e.header, e.stdout, e.stderr);
        this.directory = directory;
    }

    public String getShortMessage() {
        return "directory not found: " + directory;
    }
}
