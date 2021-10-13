/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageSizeManager;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class ConvertCharSetWindow extends Stage {

    private final ConfigInfo configInfo;
    private final ConvertCharSetPane pane;
    private final StyleManager styleManager;

    public ConvertCharSetWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.styleManager = new StyleManager(configInfo);
        this.pane = new ConvertCharSetPane(configInfo);
    }

    public void open() {
        Stage stage = this;

        Scene scene = StageSizeManager.buildRelative(stage, configInfo, pane.build(), getWindowIdentifier(), -1, -1);
        stage.setScene(scene);
        stage.setTitle("Convert charset");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                pane.close();
            }
        });
        styleManager.setStage(stage);
        stage.show();
    }

    public ConvertCharSetPane getPane() {
        return pane;
    }
    
    private void saveConfig() {
    }

    static String getWindowIdentifier() {
        return "convertcharsetwindow";
    }
}
