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
public class ErrorLogWindow {

    private Stage stage;
    private TextArea textArea;
    private final ConfigInfo configInfo;
    private final String identifier = "errorlogwindow";
    private final StyleManager styleManager;

    public ErrorLogWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.styleManager = new StyleManager(configInfo);
    }

    private void open() {
        WindowRectangle windowRectangle = null;
        if (configInfo != null) {
            windowRectangle = configInfo.getWindowRectangle(identifier);
        }

        stage = new Stage();
        double width, height;
        if (windowRectangle != null) {
            stage.setX(windowRectangle.getX());
            stage.setY(windowRectangle.getY());
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
        } else {
            width = 640;
            height = 480;
        }
        //stage.setWidth(width);
        //stage.setHeight(height);

        Scene scene = new Scene(build(), width, height);
        stage.setScene(scene);
        stage.setTitle("Error");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                configInfo.setWindowRectangle(identifier, stage.getX(), stage.getY(), stage.getScene().getWidth(), stage.getScene().getHeight());
                stage = null;
            }
        });

        styleManager.setStage(stage);
        stage.show();
    }

    public void close() {
        if (stage != null) {
            stage.close();
        }
    }

    public void appendText(String text) {
        Platform.runLater(() -> {
            checkOpen();
            textArea.appendText(text);
        });
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
                    + "Preference で適切な difftool 用コマンドを選択し直してください。";
            appendErrorMessageCommon(errorMessage, DiffToolTab.getTitle());
            return;
        }

        p = Pattern.compile("winmerge.sh: .* command not found");
        m = p.matcher(message);
        if (m.find()) {
            String errorMessage = "\n"
                    + "WinMergeU のパスが適切ではありません。\n"
                    + "Preference で適切なパスを設定してください。";
            appendErrorMessageCommon(errorMessage, ProgramsTab.getTitle());
            return;
        }
    }

    private void appendProgramError(String message) {
        appendErrorMessageCommon(message + "\nPreference で適切なパスを設定してください。", ProgramsTab.getTitle());
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

    private void checkOpen() {
        if (stage == null) {
            open();
        } else if (!stage.showingProperty().get()) {
            textArea.clear();
            stage.show();
        }
    }

    private Parent build() {
        textArea = new TextArea();
        textArea.setId("ErrorLogWindowTextArea");
        textArea.setEditable(false);
        textArea.setPrefHeight(1000);

        ButtonBar buttonBar = new ButtonBar();
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> stage.close());
        okButton.setDefaultButton(true);
        ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);
        buttonBar.getButtons().addAll(okButton);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        vbox.getChildren().addAll(textArea, buttonBar);
        return vbox;
    }
}
