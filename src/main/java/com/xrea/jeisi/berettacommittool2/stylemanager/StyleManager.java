/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.stylemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class StyleManager {

    private final ConfigInfo configInfo;
    private Parent rootNode;
    private final ChangeListener<String> fontSizeChangeListener = (observable, oldValue, newValue) -> {
        rootNode.setStyle(String.format("-fx-font-size: %spx;", newValue));
    };

    public StyleManager(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public void setStage(Stage stage) {
        setRoot(stage.getScene().getRoot());
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                close();
            }
        });
    }

    private void setRoot(Parent root) {
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
