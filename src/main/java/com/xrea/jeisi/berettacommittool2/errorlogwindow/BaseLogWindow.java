/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.errorlogwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.WindowStageManager;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
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
public abstract class BaseLogWindow {

    protected final ConfigInfo configInfo;
    //private final StyleManager styleManager;
    protected StageEx stage;
    protected TextArea textArea;

    public void appendText(String text) {
        Platform.runLater(() -> {
            checkOpen();
            textArea.appendText(text);
        });
    }

    protected void checkOpen() {
        if (stage == null) {
            open();
        } else if (!stage.showingProperty().get()) {
            textArea.clear();
            stage.show();
        }
    }
    
    protected BaseLogWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        //this.styleManager = new StyleManager(configInfo);
    }

    private void open() {
        String identifier = getIdentifier();

        stage = new StageEx(identifier);

        StageManager stageManager = new WindowStageManager();
        WindowRectangle windowRectangle = stageManager.getWindowRectangle(configInfo, identifier, 320, 240);
        Scene scene = stageManager.buildScene(build(), windowRectangle);
        stage.setScene(scene);
        stage.setTitle(getTitle());
        stageManager.open(stage, windowRectangle, configInfo);
    }

    public void close() {
        if (stage != null) {
            stage.close();
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

    protected abstract String getIdentifier();
    protected abstract String getTitle();
}
