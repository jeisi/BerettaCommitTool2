/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitCommandException extends IOException {
    protected final String header;
    protected final List<String> stdout;
    protected final List<String> stderr;
    
    public GitCommandException(String header, List<String> stdout, List<String> stderr) {
        this.header = header;
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(header);
        for(var line : stderr) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }
    
    public List<String> getStdOut() {
        return stdout;
    }
    
    public List<String> getStdErr() {
        return stderr;
    }
}
