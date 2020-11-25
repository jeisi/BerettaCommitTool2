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

    protected String[] execProcess(List<String> command, List<String> displayCommand) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        ShellScript shellScript = new ShellScript(repository);
        try {
            String cmd;
            List<String> args;
            if (command.get(0).equals("bash")) {
                cmd = getCommand(command.get(0));
                args = new ArrayList<>();
                args.add(getCommand(command.get(1)));
                args.addAll(command.subList(2, command.size()));
            } else {
                cmd = getCommand(command.get(0));
                args = command.subList(1, command.size());
            }
            shellScript.exec(cmd, args.toArray(new String[args.size()]));
            return shellScript.getOutput();
        } catch (ExecuteException ex) {
            List<String> list = Arrays.asList(shellScript.getOutputStream().toString().split("\\n"));
            GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), list, list);
            throw e;
        } catch (IOException ex) {
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

    protected String[] execProcess(String... args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        List<String> command = Arrays.asList(args);
        return execProcess(command, command);
    }

    protected String getCommand(String commandIdentifier) throws GitConfigException {
        String command = configInfo.getProgramEx(commandIdentifier);
        if (command != null) {
            return command;
        }
        return commandIdentifier;
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
