/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public interface CommandExecutor {
    public void exec(File workDir, String[] files) throws IOException, GitAPIException;
}
