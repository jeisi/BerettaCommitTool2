/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
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
public class GitCommitWindow extends Stage {

    private final GitCommitPane gitCommitPane;
    private final ConfigInfo configInfo;
    private final StyleManager styleManager;

    public GitCommitWindow(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.styleManager = new StyleManager(configInfo);
        this.gitCommitPane = new GitCommitPane(configInfo, styleManager);
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

        Scene scene = new Scene(build(), width, height);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), () -> gitCommitPane.commit());
        stage.setScene(scene);
        stage.setTitle("Commit");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                gitCommitPane.close();
            }
        });

        gitCommitPane.addEventHandler((e) -> close());
        styleManager.setStage(stage);
        
        stage.show();
        gitCommitPane.requestDefaultFocus();
    }

    private void saveConfig() {
        if (configInfo == null) {
            return;
        }

        var scene = getScene();
        configInfo.setWindowRectangle(getWindowIdentifier(), getX(), getY(), scene.getWidth(), scene.getHeight());
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
