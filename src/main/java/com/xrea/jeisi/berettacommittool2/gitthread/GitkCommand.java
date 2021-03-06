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
public class GitkCommand extends BaseSingleGitCommand {

    private boolean isAll = false;
    private boolean isSimplifyMerges = false;

    public GitkCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void setAllFlag(boolean isAll) {
        this.isAll = isAll;
    }

    public void setSimplifyMergesFlag(boolean isSimplifyMerges) {
        this.isSimplifyMerges = isSimplifyMerges;
    }

    public void log(String fileName) throws GitConfigException, IOException, InterruptedException {
        List<String> command = getGitkCommand();
        List<String> displayCommand = getGitkDisplayCommand();
        if (isAll) {
            command.add("--all");
            displayCommand.add("--all");
        }
        if (isSimplifyMerges) {
            command.add("--simplify-merges");
            displayCommand.add("--simplify-merges");
        }
        command.add(fileName);
        displayCommand.add(fileName);

        execProcess(command, displayCommand);
    }

    public void log() throws GitConfigException, IOException, InterruptedException {
        List<String> command = getGitkCommand();
        List<String> displayCommand = getGitkDisplayCommand();
        if (isAll) {
            command.add("--all");
            displayCommand.add("--all");
        }
        if (isSimplifyMerges) {
            command.add("--simplify-merges");
            displayCommand.add("--simplify-merges");
        }

        execProcess(command, displayCommand);
    }

    private List<String> getGitkCommand() {
        List<String> command = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            command.add("bash");
        }
        command.add("gitk");
        return command;
    }

    private List<String> getGitkDisplayCommand() {
        List<String> command = new ArrayList<>();
        command.add("gitk");
        return command;
    }
}
