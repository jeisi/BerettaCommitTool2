/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.errorlogwindow;

import com.xrea.jeisi.berettacommittool2.App;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import com.xrea.jeisi.berettacommittool2.exception.FaultyProgramException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.preferencewindow.DiffToolTab;
import com.xrea.jeisi.berettacommittool2.preferencewindow.ProgramsTab;
import com.xrea.jeisi.berettacommittool2.streamcaputurer.StreamCapturer;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class ErrorLogWindow extends BaseLogWindow {

    public ErrorLogWindow(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    protected String getIdentifier() {
        return "errorlogwindow";
    }

    @Override
    protected String getTitle() {
        return "Error";
    }

    public void appendException(Exception e) {
        Platform.runLater(() -> {
            checkOpen();
            boolean isEmpty = (textArea.getText().length() == 0);
            if (e instanceof GitCommandException) {
                textArea.appendText(e.getMessage());
                analyzeErrorMessage(e.getMessage());
            } else if (e instanceof FaultyProgramException) {
                appendProgramError(e.getMessage());
            } else {
                e.printStackTrace(new PrintStream(new StreamCapturer(textArea)));
            }
            if (isEmpty) {
                textArea.positionCaret(0);
            }
        });
    }

    private void analyzeErrorMessage(String message) {
        Pattern p = Pattern.compile("The diff tool (.+) is not available as");
        Matcher m = p.matcher(message);
        if (m.find()) {
            String errorMessage = "\n"
                    + m.group(1) + " は有効なコマンドではありません。\n"
                    + "Preference で適切な difftool 用コマンドを選択し直してください。\n";
            appendErrorMessageCommon(errorMessage, DiffToolTab.getTitle());
            return;
        }

        p = Pattern.compile("winmerge.sh: .* command not found");
        m = p.matcher(message);
        if (m.find()) {
            String errorMessage = "\n"
                    + "WinMergeU のパスが適切ではありません。\n"
                    + "Preference で適切なパスを設定してください。\n";
            appendErrorMessageCommon(errorMessage, ProgramsTab.getTitle());
            return;
        }
    }

    private void appendProgramError(String message) {
        appendErrorMessageCommon(message + "\nPreference で適切なパスを設定してください。\n", ProgramsTab.getTitle());
    }

    private void appendErrorMessageCommon(String message, String defaultPage) {
        textArea.appendText(message);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                App app = configInfo.getMainApp();
                app.openPreference(defaultPage);
                stage = null;
            }
        });
    }

}
