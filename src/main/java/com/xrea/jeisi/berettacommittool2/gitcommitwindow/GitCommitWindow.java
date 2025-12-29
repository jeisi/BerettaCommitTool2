/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import com.xrea.jeisi.berettacommittool2.stagemanager.ChildWindowStageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class GitCommitWindow {

    private final GitCommitPane gitCommitPane;
    private final ConfigInfo configInfo;
    //private final StyleManager styleManager;
    private final StageEx stage;

    public GitCommitWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        //this.styleManager = new StyleManager(configInfo);
        this.gitCommitPane = new GitCommitPane(configInfo, configInfo.getMainApp().getStyleManager());
        this.stage = new StageEx(getWindowIdentifier()) {
            @Override
            public void onClose() {
                gitCommitPane.close();
            }
        };
    }

    public void open() {
        StageManager stageManager = new ChildWindowStageManager();
        WindowRectangle windowRectangle = stageManager.getWindowRectangle(configInfo, getWindowIdentifier(), 640, 480);
        Scene scene = stageManager.buildScene(build(), windowRectangle);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), () -> gitCommitPane.commit());
        stage.setScene(scene);
        stage.setTitle("Commit");
//        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
//            if (oldValue == true && newValue == false) {
//                gitCommitPane.close();
//            }
//        });

        gitCommitPane.addEventHandler((e) -> close());

        //styleManager.setStage(stage);
        //stage.show();
        stageManager.open(stage, windowRectangle, configInfo);

        gitCommitPane.requestDefaultFocus();
    }

    public void close() {
        stage.close();
    }

    public void toFront() {
        stage.toFront();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return stage.showingProperty();
    }

    StageEx getStage() {
        return stage;
    }

    public GitCommitPane getGitCommitPane() {
        return gitCommitPane;
    }

    private Parent build() {
        return gitCommitPane.build();
    }

    static String getWindowIdentifier() {
        return "gitcommitwindow";
    }
}
