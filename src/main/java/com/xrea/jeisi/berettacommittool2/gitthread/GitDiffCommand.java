/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitDiffCommand {

    private final ConfigInfo configInfo;
    private final File repository;

    public GitDiffCommand(Path repository, ConfigInfo configInfo) {
        this.repository = repository.toFile();
        this.configInfo = configInfo;
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
        XmlWriter.writeStartMethod("GitDiffCommand.diff(%s)", fileName);
        String tool = getTool();
        diffCommon(fileName, tool, /*bCached=*/ false);
        XmlWriter.writeEndMethod();
    }

    public void diffCached(String fileName) throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String tool = getTool();
        diffCommon(fileName, tool, /*bCached=*/ true);
    }

    private void diffCommon(String fileName, String tool, boolean bCached) throws IOException, InterruptedException, GitCommandException, GitConfigException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(fileName, tool, bCached));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }

    protected List<String> getCommand(String fileName, String tool, boolean bCached) throws GitCommandException, GitConfigException {
        XmlWriter.writeStartMethod("GitDiffCommand.getCommand()");
        XmlWriter.writeObject("configInfo", configInfo);
        var git = configInfo.getProgram("git");
        if(git == null) {
            XmlWriter.writeEndMethodWithReturn();
            throw new GitConfigException("git のパス指定が null です。");
        }
        
        ArrayList<String> command = new ArrayList<>();
        command.add(configInfo.getProgram("git"));
        command.add("difftool");
        command.add("-y");
        command.add(tool);
        if (bCached) {
            command.add("--cached");
        }
        command.add(fileName);
        XmlWriter.writeEndMethodWithReturnValue(command.toString());
        return command;
    }

    private static String getErrorMessage(List<String> command, Process p) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("command error.");
        sb.append("\n");
        sb.append("$ ");
        sb.append(String.join(" ", command));
        sb.append("\n");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getErrorStream()))) {
            // ping結果の出力
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
