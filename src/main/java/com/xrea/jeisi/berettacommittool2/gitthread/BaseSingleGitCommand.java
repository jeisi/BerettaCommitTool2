/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class BaseSingleGitCommand {

    protected final ConfigInfo configInfo;
    protected final File repository;

    BaseSingleGitCommand(Path repository, ConfigInfo configInfo) {
        this.repository = repository.toFile();
        this.configInfo = configInfo;
    }

    protected void execProcess(List<String> args, List<String> displayCommand) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(args));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), getInputStream(process), getErrorStream(process));
            throw e;
        }
    }

    protected void execProcess(String... args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        List<String> command = Arrays.asList(args);
        execProcess(command, command);
    }

    protected List<String> execProcessWithOutput(List<String> command, List<String> displayCommand) throws IOException, InterruptedException, GitConfigException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(command));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), getInputStream(process), getErrorStream(process));
            throw e;
        }
        return getInputStream(process);
    }

    protected List<String> execProcessWithOutput(String... args) throws IOException, InterruptedException, GitConfigException {
        List<String> command = Arrays.asList(args);
        return execProcessWithOutput(command, command);
    }

    protected String[] getCommand(String... args) throws GitConfigException {
        String command = configInfo.getProgramEx(args[0]);
        if (command != null) {
            args[0] = command;
        }
        return args;
    }

    protected List<String> getCommand(List<String> args) throws GitConfigException {
        String command = configInfo.getProgramEx(args.get(0));
        if (command != null) {
            args.set(0, command);
        }
        return args;
    }

    /*
    protected String getErrorMessage(List<String> command, Process p) throws IOException {
        StringBuilder sb = new StringBuilder();
        appendErrorMessageHeader(sb, String.join(" ", command));
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
     */
    protected static List<String> getErrorStream(Process p) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getErrorStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
        }
        return lines;
    }

    protected static List<String> getInputStream(Process p) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
        }
        return lines;
    }

    protected String getErrorMessageHeader(List<String> command) {
        StringBuilder builder = new StringBuilder();
        builder.append("command error:\n");
        builder.append("[");
        builder.append(repository.toString());
        builder.append("]\n");
        builder.append("$ ");
        builder.append(String.join(" ", command));
        builder.append("\n");
        return builder.toString();
    }

}
