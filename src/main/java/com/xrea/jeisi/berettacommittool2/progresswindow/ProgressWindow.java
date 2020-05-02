/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.progresswindow;

import com.sun.glass.ui.Cursor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class ProgressWindow extends Stage implements CompleteListener {

    private ListView<ProgressModel> listView;
    private boolean created = false;

    public void open() {
        if (!created) {
            Scene scene = new Scene(build(), 480, 320);
            Stage stage = this;
            stage.setScene(scene);
            stage.setTitle("Progress Window");
            stage.show();
            created = true;
        } else {
            show();
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
                        hide();
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
}
