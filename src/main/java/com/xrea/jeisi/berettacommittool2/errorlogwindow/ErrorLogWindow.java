/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.errorlogwindow;

import com.xrea.jeisi.berettacommittool2.streamcaputurer.StreamCapturer;
import java.io.PrintStream;
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

    private void open() {
        Scene scene = new Scene(build(), 640, 480);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Error");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                stage = null;
            }
        });
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
            e.printStackTrace(new PrintStream(new StreamCapturer(textArea)));
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
