/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jeisi
 */
public class GitLogCommand {

    private final File repository;

    public GitLogCommand(File repository) {
        this.repository = repository;
    }

    public String getLog() throws IOException {
        return "";
    }
}
