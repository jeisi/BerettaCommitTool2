/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitRestoreCommand extends BaseMultiGitCommand {

    private final VersionInfo ver2230 = new VersionInfo(2, 23, 0);
    
    public GitRestoreCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    // git restore --source=HEAD --staged --worktree <file>...
    public void restoreReset(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        VersionInfo versionInfo = configInfo.getVersionInfo();
        if(versionInfo == null) {
            throw new GitConfigException("VersionInfo の値がセットされていません。");
        }
        
        if(versionInfo.compareTo(ver2230) >= 0) {
            // ver.2.23.0 以降は git restore コマンドが使える。
            restoreResetNative(datas);
        } else {
            // ver.2.23.0 前は git restore コマンドを使用できない。
            restoreResetCompatible(datas);
        }
    }

    private void restoreResetNative(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> {
            List<String> command = Arrays.asList("git", "restore", "--source=HEAD", "--staged", "--worktree", data.getFileName());
            List<String> displayCommand = Arrays.asList("git", "restore", "--source=HEAD", "--staged", "--worktree", data.getFileName());
            execProcess(command, displayCommand);
        });
    }

    private void restoreResetCompatible(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        XmlWriter.writeStartMethod("GitRestoreCommand.restoreResetCompatible()");
        execEachFile(datas, (data) -> {
            execProcess("git", "reset", "HEAD", data.getFileName());
            execProcess("git", "checkout", "--", data.getFileName());
        });
        XmlWriter.writeEndMethod();
    }
}
