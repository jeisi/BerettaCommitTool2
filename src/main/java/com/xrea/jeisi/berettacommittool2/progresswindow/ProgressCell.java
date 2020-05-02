/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.progresswindow;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author jeisi
 */
public class ProgressCell extends ListCell<ProgressModel> {

    private VBox cellContainer;
    private Text titleText;
    private Label countLabel;
    private ProgressBar progressBar;

    public ProgressCell() {
        initComponent();
    }

    private void initComponent() {
        titleText = new Text();
        VBox.setVgrow(titleText, Priority.NEVER);

        countLabel = new Label();

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        
        HBox hbox = new HBox(countLabel, progressBar);

        cellContainer = new VBox();
        cellContainer.getChildren().addAll(titleText, hbox);
    }

    @Override
    protected void updateItem(ProgressModel progressModel, boolean empty) {
        super.updateItem(progressModel, empty);
        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            titleText.setText(progressModel.getTitle());
            countLabel.textProperty().bind(progressModel.countTextProperty());
            progressBar.progressProperty().bind(progressModel.progressProperty());
            setGraphic(cellContainer);
        }
    }
}
