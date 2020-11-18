/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author jeisi
 */
public class GitCheckIgnoreCommand extends BaseSingleGitCommand {

    public GitCheckIgnoreCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public String checkIgnore(String fileName) throws IOException, GitConfigException, InterruptedException {
        StringBuilder builder = new StringBuilder();
        String[] result = execProcess("git", "check-ignore", "-v", fileName);
        builder.append("[");
        builder.append(repository.toString());
        builder.append("]\n");
        builder.append("$ git check-ignore ");
        builder.append(fileName);
        builder.append("\n");
        builder.append(String.join("\n", result));
        return builder.toString();
    }
}
