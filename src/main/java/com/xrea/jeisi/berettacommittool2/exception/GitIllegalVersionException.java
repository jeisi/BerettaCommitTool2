/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.exception;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitIllegalVersionException extends GitCommandException {

    public GitIllegalVersionException(List<String> stderr) {
        super("", null, stderr);
    }
}
