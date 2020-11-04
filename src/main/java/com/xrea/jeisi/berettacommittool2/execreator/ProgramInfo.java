/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class ProgramInfo {

    private final String identifier;
    private final String exe;
    private final List<String> candidates;

    public ProgramInfo(String identifier, String exe, List<String> candidates) {
        this.identifier = identifier;
        this.exe = exe;
        this.candidates = candidates;
    }

    public ProgramInfo(String identifier, String exe, String[] candidates) {
        this.identifier = identifier;
        this.exe = exe;
        this.candidates = Arrays.asList(candidates);
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getExe() {
        return exe;
    }
    
    public List<String> getCandidates() {
        return candidates;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(identifier);
        builder.append(",");
        builder.append(exe);
        builder.append(",");
        builder.append("{");
        builder.append(String.join(",", candidates));
        builder.append("}");
        builder.append("}");
        return builder.toString();
    }
}
