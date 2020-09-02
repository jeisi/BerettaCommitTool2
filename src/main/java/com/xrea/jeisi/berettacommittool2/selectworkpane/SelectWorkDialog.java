/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import java.awt.BorderLayout;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkDialog extends Alert {

    private final SelectWorkPane selectWorkPane;

    public SelectWorkDialog(Stage stage) {
        super(AlertType.CONFIRMATION);

        selectWorkPane = new SelectWorkPane(stage);
        setTitle("Select working directory");
        setHeaderText(null);
        getDialogPane().setContent(selectWorkPane.build());
    }

    public SelectWorkPane getSelectWorkPane() {
        return selectWorkPane;
    }    
}
