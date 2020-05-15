/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class GitCommitWindow extends Stage {

    private GitCommitPane gitCommitPane = new GitCommitPane();
    private ConfigInfo configInfo;

    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        gitCommitPane.setConfigInfo(configInfo);
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
        stage.setScene(scene);
        stage.setTitle("Commit");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                gitCommitPane.close();
            }
        });

        stage.show();
    }

    private void saveConfig() {
        System.out.println("GitCommitWindow.saveConfig()");
        if (configInfo == null) {
            return;
        }

        System.out.println("setWindowRectangle()");
        configInfo.setWindowRectangle(getWindowIdentifier(), getX(), getY(), getWidth(), getHeight());
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
