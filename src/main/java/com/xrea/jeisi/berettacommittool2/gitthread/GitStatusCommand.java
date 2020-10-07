/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class GitStatusCommand {

    private final ConfigInfo configInfo;
    private final File repository;

    public GitStatusCommand(Path repoDir, ConfigInfo configInfo) {
        this.repository = repoDir.toFile();
        this.configInfo = configInfo;
    }

    public List<GitStatusData> status(RepositoryData repositoryData) throws GitCommandException, GitConfigException, IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(getCommand((String[]) null));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        List<GitStatusData> status = getStatusDatas(process, repositoryData);
        return status;
    }

    public List<GitStatusData> status(RepositoryData repositoryData, String... paths) throws IOException, GitCommandException, GitConfigException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(paths));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        return getStatusDatas(process, repositoryData);
    }

    private List<String> getCommand(String... paths) throws GitCommandException, GitConfigException {
        var git = configInfo.getProgramEx("git");
        ArrayList<String> command = new ArrayList<>();
        command.add(git);
        command.add("status");
        command.add("-s");
        command.add("--untracked-files=all");
        if(paths != null) {
            command.addAll(Arrays.asList(paths));
        } 
        return command;
    }

    private static List<GitStatusData> getStatusDatas(Process p, RepositoryData repositoryData) throws IOException {
        XmlWriter.writeStartMethod("GitStatusCommand.getStatusDatas()");
        Pattern pattern = Pattern.compile("^(.)(.) (.+)( -> (.+))?");
        List<GitStatusData> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                Matcher m = pattern.matcher(line);
                if (!m.matches()) {
                    throw new AssertionError(line + " が正規表現にマッチしませんでした。");
                }

                String index = m.group(1);
                if(index.equals(" ")) {
                    index = "";
                }
                
                String workTree = m.group(2);
                if(workTree.equals(" ")) {
                    workTree = "";
                }
                
                String fileName;
                if (m.group(4) == null) {
                    fileName = m.group(3);
                } else {
                    fileName = m.group(5);
                }
                list.add(new GitStatusData(index, workTree, fileName, repositoryData));
            }
        }
        XmlWriter.writeEndMethod();
        return list;
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
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
