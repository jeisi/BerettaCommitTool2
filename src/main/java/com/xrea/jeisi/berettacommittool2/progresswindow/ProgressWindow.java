/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.progresswindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.WindowStageManager;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class ProgressWindow implements CompleteListener {
    private ListView<ProgressModel> listView;
    private boolean created = false;
    private final ConfigInfo configInfo;
    //private final StyleManager styleManager;
    private final Stage stage = new Stage();

    public ProgressWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        //this.styleManager = new StyleManager(configInfo);
    }
    
    public void open() {
        if (!created) {
            StageManager stageManager = new WindowStageManager(configInfo, getWindowIdentifier(), 480, 320);
            Scene scene = stageManager.buildScene(build());
            stage.setScene(scene);
            stage.setTitle("Progress Window");
            stageManager.open(stage);
            created = true;
        } else {
            stage.show();
        }
    }

    public void addProgressModel(ProgressModel model) {
        listView.getItems().add(model);
        model.setCompleteListener(this);
    }

    @Override
    public void complete(ProgressModel model) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                Platform.runLater(() -> {
                    listView.getItems().remove(model);
                    if(listView.getItems().isEmpty()) {
                        stage.hide();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private Parent build() {
        listView = new ListView<>();
        listView.setId("progressWindowListView");
        listView.setCellFactory(e -> {
            return new ProgressCell();
        });
        return listView;
    }

    private String getWindowIdentifier() {
        return "progresswindow";
    }
}
