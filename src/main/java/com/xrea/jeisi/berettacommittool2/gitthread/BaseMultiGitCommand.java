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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

/**
 *
 * @author jeisi
 */
public abstract class BaseMultiGitCommand extends BaseSingleGitCommand {

    protected ProgressWindow progressWindow;
    protected ProgressModel progressModel = null;

    public BaseMultiGitCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public final void setProgressWindow(ProgressWindow progressWindow) {
        this.progressWindow = progressWindow;
    }

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
