/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class RepositoriesLoader {

    private final Path file;
    protected List<String> lines;

    public RepositoriesLoader(Path file) {
        this.file = file;
    }

    private boolean isExistDirectory() {
        return Files.exists(file.getParent());
    }

    private boolean isExistFile() {
        return Files.exists(file);
    }

    public List<String> getLines() {
        return lines;
    }

    public void load() throws IOException {
        if (!isExistDirectory()) {
            lines = new ArrayList<>();
            return;
        }

        if (!isExistFile()) {
            lines = new ArrayList<>();
            lines.add(".");
            return;
        }

        lines = Files.readAllLines(file);
    }
}
