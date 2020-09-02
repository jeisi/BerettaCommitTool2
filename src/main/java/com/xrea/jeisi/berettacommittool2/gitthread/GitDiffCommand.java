/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

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

    public void diff(String fileName) throws IOException, InterruptedException, GitCommandException {
        diffCommon(fileName, /*bCached=*/ false);
    }
    
    public void diffCached(String fileName) throws IOException, InterruptedException, GitCommandException {
        diffCommon(fileName, /*bCached=*/ true);
    }
    
    private void diffCommon(String fileName, boolean bCached) throws IOException, InterruptedException, GitCommandException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(fileName, bCached));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
    }
    
    protected List<String> getCommand(String fileName, boolean bCached) {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("difftool");
        command.add("-y");
        command.add("--tool=meld");
        //command.add("--tool=vimdiff");
        if(bCached) {
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
