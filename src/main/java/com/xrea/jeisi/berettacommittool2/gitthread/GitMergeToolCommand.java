/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.shellscript.ShellScript;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.apache.commons.exec.DefaultExecuteResultHandler;

/**
 *
 * @author jeisi
 */
public class GitMergeToolCommand extends BaseSingleGitCommand {

    public GitMergeToolCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void exec(String fileName) throws GitConfigException, IOException, InterruptedException {
        String tool = getTool();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PipedOutputStream pw = new PipedOutputStream();
        PipedInputStream pi = new PipedInputStream(pw);
        try {
            System.setIn(pi);

            ShellScript shellScript = new ShellScript(repository);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            shellScript.setOutputStream(outputStream);
            shellScript.setInputStream(System.in);
            shellScript.setResultHandler(resultHandler);
            shellScript.exec(getCommand("git"), new String[]{"mergetool", tool, fileName});

            boolean wasQuestion = false;
            int size = -1;
            while (!resultHandler.hasResult()) {
                Thread.sleep(1000);
                if (size == outputStream.size()) {
                    continue;
                }
                size = outputStream.size();
                //XmlWriter.writeObject("outputStream", outputStream.toString());
                if (outputStream.toString().endsWith("[y/n]? ") && !wasQuestion) {
                    wasQuestion = true;
                    Platform.runLater(() -> confirm(fileName, pw));
                }
            }
        } finally {
            XmlWriter.writeMessage("finally");
            pi.close();
            pw.close();
            outputStream.close();
        }
    }

    private String getTool() {
        String tool = configInfo.getMergeTool();
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

    private void confirm(String fileName, PipedOutputStream pw) {
        String message = String.format("%s は変更されていませんが、マージは成功しましたか？\n（WinMerge の右側の内容をそのまま採用でよろしいですか？）", fileName);
        Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        String answer;
        if (!result.isPresent() || result.get() == ButtonType.NO) {
            // NO
            answer = "n\r\n";
        } else {
            // YES
            answer = "y\r\n";
        }
        try {
            pw.write(answer.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(GitMergeToolCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
