/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

import java.util.Arrays;

/**
 *
 * @author jeisi
 */
public class GitCommitNoMessageException extends GitCommandException {
    public GitCommitNoMessageException(String message) {
        super("", null, Arrays.asList(message));
    }
    
}
