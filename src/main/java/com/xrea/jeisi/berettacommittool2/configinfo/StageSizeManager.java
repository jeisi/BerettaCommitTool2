/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class StageSizeManager {

    public static Scene build(Stage stage, ConfigInfo configInfo, Parent parent, String identifier, double defaultWidth, double defaultHeight) {
        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(identifier) : null;
        double width, height;
        Scene scene;
        if (windowRectangle != null) {
            stage.setX(windowRectangle.getX());
            stage.setY(windowRectangle.getY());
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            scene = new Scene(parent, width, height);
        } else {
            if(defaultWidth < 0 || defaultHeight < 0) {
                scene = new Scene(parent);
            } else {
                scene = new Scene(parent, defaultWidth, defaultHeight);
            }
        }
        
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                configInfo.setWindowRectangle(identifier, stage.getX(), stage.getY(), scene.getWidth(), scene.getHeight());
            }
        });

        return scene;
    }
}
