/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stagemanager.ChildWindowStageManager;
import com.xrea.jeisi.berettacommittool2.stagemanager.StageManager;
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
    private final Stage stage;

    public GitCommitWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        //this.styleManager = new StyleManager(configInfo);
        this.gitCommitPane = new GitCommitPane(configInfo, configInfo.getMainApp().getStyleManager());
        this.stage = new Stage();
    }

    public void open() {
        StageManager stageManager = new ChildWindowStageManager(configInfo, getWindowIdentifier(), 640, 480) {
            @Override
            public void onClosed() {
                gitCommitPane.close();
            }
        };
        Scene scene = stageManager.buildScene(build());
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), () -> gitCommitPane.commit());
        stage.setScene(scene);
        stage.setTitle("Commit");

        gitCommitPane.addEventHandler((e) -> close());

        //styleManager.setStage(stage);
        //stage.show();
        stageManager.open(stage);

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

    Stage getStage() {
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
