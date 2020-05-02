/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class GitCommitWindow extends Stage {

    private GitCommitPane gitCommitPane = new GitCommitPane();
    
    public void open() {
        Scene scene = new Scene(build(), 640, 480);
        Stage stage = this;
        stage.setScene(scene);
        stage.setTitle("Commit");
        stage.show();
    }
    
    @Override
    public void close() {
        super.close();
        gitCommitPane.close();
    }
    
    public GitCommitPane getGitCommitPane() {
        return gitCommitPane;
    }
    
    private Parent build() {
        return gitCommitPane.build();
    }
}
