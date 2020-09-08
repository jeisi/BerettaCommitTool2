/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitDiffCommand {

    private final File repository;

    public GitDiffCommand(File repository) {
        this.repository = repository;
    }

    public static String getTool(ConfigInfo configInfo) {
        String tool = configInfo.getDiffTool();
        switch (tool) {
            case "meld":
            case "p4merge":
            case "vimdiff":
                return String.format("--tool=%s", tool);
            case "winmerge":
                return String.format("--extcmd=%s/bin/winmerge.sh", configInfo.getPath().getParent().toString());
            default:
                throw new IllegalArgumentException(tool + "に対応する case 文がありません。");
        }
    }

    public void diff(String fileName, String tool) throws IOException, InterruptedException, GitCommandException {
        diffCommon(fileName, tool, /*bCached=*/ false);
    }

    public void diffCached(String fileName, String tool) throws IOException, InterruptedException, GitCommandException {
        diffCommon(fileName, tool, /*bCached=*/ true);
    }

    private void diffCommon(String fileName, String tool, boolean bCached) throws IOException, InterruptedException, GitCommandException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(fileName, tool, bCached));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }

    protected List<String> getCommand(String fileName, String tool, boolean bCached) {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("difftool");
        command.add("-y");

        if (bCached) {
            command.add("--cached");
        }
        command.add(fileName);
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
