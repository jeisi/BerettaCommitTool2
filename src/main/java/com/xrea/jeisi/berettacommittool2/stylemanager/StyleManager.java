/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.stylemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 *
 * @author jeisi
 */
public class StyleManager {

    private final ConfigInfo configInfo;
    private final String styleExternalForm;
    private Parent rootNode;
    private final ChangeListener<String> fontSizeChangeListener = (observable, oldValue, newValue) -> {
        if(Objects.equals(newValue, ConfigInfo.SYSTEM_FONT_SIZE_TEXT)) {
            rootNode.setStyle(null);
        } else if(newValue != null) {
            rootNode.setStyle(String.format("-fx-font-size: %spx;", newValue));
        } else {
            rootNode.setStyle(null);
        }
    };

    public StyleManager(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.styleExternalForm = getClass().getResource("/style.css").toExternalForm();
    }

    public void setStage(Stage stage) {
        setRoot(stage.getScene().getRoot());
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                close();
            }
        });
    }

    public void styleDialog(DialogPane pane) {
        pane.getStylesheets().add(styleExternalForm);

        var fontSize = configInfo.getFontSize();
        if (fontSize != null) {
            pane.setStyle(String.format("-fx-font-size: %spx;", fontSize));
        }
    }

    private void setRoot(Parent root) {
        root.getStylesheets().add(styleExternalForm);

        rootNode = root;
        var fontSize = configInfo.getFontSize();
        if (fontSize != null) {
            fontSizeChangeListener.changed(null, "", fontSize);
        }
        configInfo.fontSizeProperty().addListener(fontSizeChangeListener);
    }

    private void close() {
        configInfo.fontSizeProperty().removeListener(fontSizeChangeListener);
    }
}
