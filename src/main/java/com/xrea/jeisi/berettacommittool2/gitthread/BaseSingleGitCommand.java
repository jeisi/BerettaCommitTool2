/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.DirectoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.shellscript.ShellScript;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.exec.ExecuteException;

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
        //XmlWriter.writeObject("args", args);
        //XmlWriter.writeObject("repository", repository.toString());
        ShellScript shellScript = new ShellScript(repository);
        try {
            shellScript.exec(getCommand(args.get(0)), args.subList(1, args.size()).toArray(new String[args.size() - 1]));
        } catch (ExecuteException ex) {
            List<String> list = Arrays.asList(shellScript.getOutputStream().toString().split("\\n"));
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), list, list);
            throw e;
        } catch (IOException ex) {
            //XmlWriter.writeObject("ex.getMessage()", ex.getMessage());
            Pattern p = Pattern.compile("(.+) doesn't exist.");
            Matcher m = p.matcher(ex.getMessage());
            if (m.matches()) {
                List<String> list = Arrays.asList(ex.getMessage());
                GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), list, list);
                throw new DirectoryNotFoundException(m.group(1), e);
            }
            throw ex;
        }
    }

    protected void execProcess(String... args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        List<String> command = Arrays.asList(args);
        execProcess(command, command);
    }

    protected String[] execProcessWithOutput(List<String> command, List<String> displayCommand) throws IOException, InterruptedException, GitConfigException {
        ShellScript shellScript = new ShellScript(repository);
        try {
            return shellScript.execWithOutput(getCommand(command.get(0)), command.subList(1, command.size()).toArray(new String[command.size() - 1]));
        } catch (ExecuteException ex) {
            List<String> list = Arrays.asList(shellScript.getOutputStream().toString().split("\\n"));
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), list, list);
            throw e;
        }
        /*
        ProcessBuilder pb = new ProcessBuilder(getCommand(command));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), getInputStream(process), getErrorStream(process));
            throw e;
        }
        return getInputStream(process);
         */
    }

    protected String[] execProcessWithOutput(String... args) throws IOException, InterruptedException, GitConfigException {
        List<String> command = Arrays.asList(args);
        return execProcessWithOutput(command, command);
    }

    protected String getCommand(String commandIdentifier) throws GitConfigException {
        String command = configInfo.getProgramEx(commandIdentifier);
        if (command != null) {
            return command;
        }
        return commandIdentifier;
    }

    /*
    protected List<String> getCommand(List<String> args) throws GitConfigException {
        String command = configInfo.getProgramEx(args.get(0));
        if (command != null) {
            args.set(0, command);
        }
        return args;
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

    private String getErrorMessage(OutputStream out) {
        return "command error:\n" + out.toString();
    }
}
