/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitCommandException extends GitAPIException {
    public GitCommandException(String message) {
        super(message);
    }
}
