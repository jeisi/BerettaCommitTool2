/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.WindowStageManager;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class PreferenceWindow {

    private final ConfigInfo configInfo;
    private PreferencePane pane;
    private Stage stage;

    public PreferenceWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }
    
    public void open() {
        open(/*defualtTab=*/ null);
    }

    public void open(String defaultTab) {
        if(stage != null) {
            stage.requestFocus();
            return;
        }

        stage = new Stage();
        pane = new PreferencePane(stage, configInfo, defaultTab);

        StageManager stageManager = new WindowStageManager(configInfo, getWindowIdentifier(), 640, 480);
        Scene scene = stageManager.buildScene(pane.build());
        stage.setScene(scene);
        stage.setTitle("Preference");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                pane.close();
                stage = null;
            }
        });

        pane.addEventHandler((e) -> stage.close());

        stageManager.open(stage);
    }
    
    public void close() {
        if(stage == null) {
            return;
        }
        
        stage.close();
    }
    
    public double getX() {
        return stage.getX();
    }
    
    public double getY() {
        return stage.getY();
    }
    
    public Scene getScene() {
        return stage.getScene();
    }
    
    public void setX(double x) {
        stage.setX(x);
    }
    
    public void setY(double y) {
        stage.setY(y);
    }
    
    public void setWidth(double width) {
        stage.setWidth(width);
    }
    
    public void setHeight(double height) {
        stage.setHeight(height);
    }
    
    public boolean isShowing() {
        return stage.isShowing();
    }

    private void saveConfig() {
        if (configInfo == null) {
            return;
        }

        //var scene = getScene();
        //configInfo.setWindowRectangle(getWindowIdentifier(), getX(), getY(), scene.getWidth(), scene.getHeight());
    }

    static String getWindowIdentifier() {
        return "preferencewindow";
    }
}
