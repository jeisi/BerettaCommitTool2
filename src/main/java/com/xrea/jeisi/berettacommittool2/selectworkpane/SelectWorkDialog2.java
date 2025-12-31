/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stagemanager.DialogStageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;

import java.util.Optional;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkDialog2 {

    private final ConfigInfo configInfo;
    private final Stage stage;
    private final SelectWorkPane2 selectWorkPane;
    //private final StyleManager styleManager;

    public SelectWorkDialog2(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.stage = new Stage();
        this.selectWorkPane = new SelectWorkPane2(stage, configInfo);
        //this.styleManager = new StyleManager(configInfo);
    }

    public Optional<ButtonType> showAndWait() {
        selectWorkPane.addEventHandler((e) -> stage.close());

        StageManager stageManager = new DialogStageManager(configInfo, getWindowIdentifier(), 640, 480);
        Scene scene = stageManager.buildScene(build());
        stage.setScene(scene);
        stage.setTitle("Select working directory");
        //selectWorkPane.requestFocus();

        stage.initModality(Modality.APPLICATION_MODAL);
        stageManager.open(stage);

        return selectWorkPane.getResult();
    }

    public String getCurrentDirectory() {
        return selectWorkPane.getCurrentDirectory();
    }

    private Parent build() {
        return selectWorkPane.build();
    }

    static String getWindowIdentifier() {
        return "selectworkdialog";
    }
}
