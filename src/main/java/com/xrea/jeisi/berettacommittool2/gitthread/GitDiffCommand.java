/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.IOException;
import java.nio.file.Path;

/**
 *
 * @author jeisi
 */
public class GitDiffCommand extends BaseSingleGitCommand {

    public GitDiffCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    private String getTool() {
        String tool = configInfo.getDiffTool();
        switch (tool) {
            case "meld":
            case "p4merge":
            case "vimdiff":
                return String.format("--tool=%s", tool);
            case "winmerge":
                return String.format("--extcmd=%s/bin/winmerge.sh", configInfo.getAppDir());
            default:
                throw new IllegalArgumentException(tool + "に対応する case 文がありません。");
        }
    }

    public void diff(String fileName) throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String tool = getTool();
        //diffCommon(fileName, tool, /*bCached=*/ false);
        execProcess("git", "difftool", "-y", tool, fileName);
    }

    public void diffCached(String fileName) throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String tool = getTool();
        //diffCommon(fileName, tool, /*bCached=*/ true);
        execProcess("git", "difftool", "-y", tool, "--cached", fileName);
    }
}
