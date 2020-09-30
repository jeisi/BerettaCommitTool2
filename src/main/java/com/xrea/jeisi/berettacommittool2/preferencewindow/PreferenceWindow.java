/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class PreferenceWindow extends Stage {

    private final ConfigInfo configInfo;
    private final PreferencePane pane;

    public PreferenceWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        pane = new PreferencePane(this, configInfo);
    }

    public void open() {
        Stage stage = this;

        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(getWindowIdentifier()) : null;
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

        Scene scene = new Scene(pane.build(), width, height);
        stage.setScene(scene);
        stage.setTitle("Preference");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                pane.close();
            }
        });

        pane.addEventHandler((e) -> close());

        stage.show();
    }

    private void saveConfig() {
        if (configInfo == null) {
            return;
        }

        var scene = getScene();
        configInfo.setWindowRectangle(getWindowIdentifier(), getX(), getY(), scene.getWidth(), scene.getHeight());
    }

    static String getWindowIdentifier() {
        return "preferencewindow";
    }
}
