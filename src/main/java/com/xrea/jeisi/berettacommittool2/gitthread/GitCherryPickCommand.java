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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitCherryPickCommand extends BaseSingleGitCommand {

    public GitCherryPickCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void cherryPick(String option) throws GitConfigException, IOException, InterruptedException {
        List<String> command = getCherryPickCommand(option);
        List<String> displayCommand = getCherryPickCommand(option);
        execProcess(command, displayCommand);
    }

    private List<String> getCherryPickCommand(String option) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("cherry-pick");
        command.add(option);
        return command;
    }
}
