/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author jeisi
 */
public interface CommandExecutor2 {

    public void exec(Path workDir, List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException;

}
