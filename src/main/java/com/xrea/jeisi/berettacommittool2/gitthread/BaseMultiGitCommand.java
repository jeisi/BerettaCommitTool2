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
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;

/**
 *
 * @author jeisi
 */
public abstract class BaseGitCommand {

    protected ProgressWindow progressWindow;
    protected ProgressModel progressModel = null;
    protected final ConfigInfo configInfo;
    protected final File repository;

    public BaseGitCommand(Path repository, ConfigInfo configInfo) {
        this.repository = repository.toFile();
        this.configInfo = configInfo;
    }

    public final void setProgressWindow(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

    /*
    protected void execEachFile(String[] files, ProcessCommand command) throws IOException, GitConfigException, InterruptedException {
        XmlWriter.writeStartMethod("BseGitCommand.execEachFile(%s)", Arrays.toString(files));

        if (progressWindow != null && files.length > 1) {
            progressModel = new ProgressModel(String.format("git checkout -- %s ...", files[0]), files.length);
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        int currentValue = 0;
        for (var file : files) {
            command.exec(file);
            ++currentValue;
            if (progressModel != null) {
                Platform.runLater(new SetCurrentValue(progressModel, currentValue));
            }
        }

        XmlWriter.writeEndMethod();
    }
    */

    protected void execEachFile(List<GitStatusData> datas, ProcessCommand2 command) throws IOException, GitConfigException, InterruptedException {
        if (progressWindow != null && datas.size() > 1) {
            progressModel = new ProgressModel(String.format("git checkout -- %s ...", datas.get(0).getFileName()), datas.size());
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        int currentValue = 0;
        for (var data : datas) {
            command.exec(data);
            ++currentValue;
            if (progressModel != null) {
                Platform.runLater(new SetCurrentValue(progressModel, currentValue));
            }
        }
    }

    protected void execProcess(String... args) throws GitConfigException, IOException, InterruptedException, GitCommandException {
        XmlWriter.writeStartMethod("BaseGitCommand.execProcess(%s)", Arrays.toString(args));

        ProcessBuilder pb = new ProcessBuilder(getCommand(args));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }

        XmlWriter.writeEndMethod();
    }

    protected List<String> execProcessWithOutput(String... args) throws IOException, InterruptedException, GitConfigException {
        ProcessBuilder pb = new ProcessBuilder(getCommand(args));
        pb.directory(repository);
        Process process = pb.start();
        int ret = process.waitFor();
        if (ret != 0) {
            GitCommandException e = new GitCommandException(getErrorMessage(pb.command(), process));
            throw e;
        }
        return getInputStream(process);
    }

    protected String[] getCommand(String... args) throws GitConfigException {
        String command = configInfo.getProgramEx(args[0]);
        if (command != null) {
            args[0] = command;
        }
        return args;
    }

    protected static String getErrorMessage(List<String> command, Process p) throws IOException {
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
}
