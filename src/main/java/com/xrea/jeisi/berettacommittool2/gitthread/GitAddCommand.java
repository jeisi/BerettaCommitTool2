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
import com.xrea.jeisi.berettacommittool2.shellscript.ShellScript;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteResultHandler;

/**
 *
 * @author jeisi
 */
public class GitAddCommand extends BaseMultiGitCommand {

    public GitAddCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void add(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.add()");

        execEachFile(datas, (data) -> execProcess("git", "add", data.getFileName()));

        XmlWriter.writeEndMethod();
    }

    public void add(GitStatusData data) throws IOException, GitConfigException, InterruptedException {
        add(Arrays.asList(data));
    }

    // git add -u
    public void addUpdate() throws GitConfigException, IOException, InterruptedException {
        addFilesByOption("-u");
    }

    // git add -A
    public void addAll() throws GitConfigException, IOException, InterruptedException {
        addFilesByOption("-A");
    }

    private void addFilesByOption(String option) throws GitConfigException, IOException, InterruptedException {
        XmlWriter.writeStartMethod("GitAddCommand.addFilesByOption()");
        String[] lines = execProcessWithOutput("git", "add", "--dry-run", option);
        if (progressWindow != null && lines.length > 1) {
            progressModel = new ProgressModel(String.format("git %s ...", lines[0]), lines.length);
            Platform.runLater(() -> {
                progressWindow.open();
                progressWindow.addProgressModel(progressModel);
            });
        }

        PipedOutputStream pw = new PipedOutputStream();
        PipedInputStream pi = new PipedInputStream(pw);
        BufferedReader reader = new BufferedReader(new InputStreamReader(pi));
        try {
            ShellScript shellScript = new ShellScript(repository);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            shellScript.setOutputStream(pw);
            shellScript.setResultHandler(resultHandler);
            shellScript.exec(getCommand("git"), new String[]{"add", "-v", option});

            Pattern p = Pattern.compile("add '.*'");
            String line;
            int currentValue = 0;
            XmlWriter.writeMessage("-------------");
            while ((line = reader.readLine()) != null) {
                XmlWriter.writeObject("line", line);
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    ++currentValue;
                    if (progressModel != null) {
                        Platform.runLater(new SetCurrentValue(progressModel, currentValue));
                    }
                }
                //Thread.sleep(1000);
            }

            resultHandler.waitFor();
            if (resultHandler.getExitValue() != 0) {
                List<String> displayCommand = Arrays.asList("git", "add", option);
                List<String> list = Arrays.asList(shellScript.getOutputStream().toString().split("\\n"));
                GitCommandException e = new GitCommandException(getErrorMessageHeader(displayCommand), list, list);
                throw e;
            }
        } finally {
            reader.close();
            pi.close();
            pw.close();
            XmlWriter.writeEndMethod();
        }
    }
}
