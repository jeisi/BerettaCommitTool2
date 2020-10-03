/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import java.awt.BorderLayout;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkDialog {

    private final ConfigInfo configInfo;
    private final SelectWorkPane selectWorkPane;
    private final Stage stage;
    private Optional<ButtonType> result = Optional.empty();
    private final StyleManager styleManager;

    public SelectWorkDialog(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.stage = new Stage();
        this.selectWorkPane = new SelectWorkPane(stage);
        this.styleManager = new StyleManager(configInfo);
    }

    public boolean isShowing() {
        return stage.isShowing();
    }
    
    public Optional<ButtonType> showAndWait() {
        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(getWindowIdentifier()) : null;
        double width, height;
        Scene scene;
        if (windowRectangle != null) {
            stage.setX(windowRectangle.getX());
            stage.setY(windowRectangle.getY());
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            scene = new Scene(build(), width, height);
        } else {
            scene = new Scene(build());
        }
        styleManager.setRoot(scene.getRoot());

        stage.setScene(scene);
        stage.setTitle("Select working directory");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                close();
            }
        });

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return result;
    }

    private void close() {
        saveConfig();
        styleManager.close();
    }

    private Parent build() {
        Button okButton = new Button("OK");
        okButton.setOnAction(eh -> clickOk());
        ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(eh -> clickCancel());
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(okButton, cancelButton);

        Parent centerPane = selectWorkPane.build();
        BorderPane.setMargin(centerPane, new Insets(5));
        BorderPane.setMargin(buttonBar, new Insets(5, 5, 5, 5));
        
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(centerPane);
        borderPane.setBottom(buttonBar);

        return borderPane;
    }

    private void clickOk() {
        result = Optional.of(ButtonType.OK);
        stage.close();
    }

    private void clickCancel() {
        result = Optional.of(ButtonType.CANCEL);
        stage.close();
    }

    public SelectWorkPane getSelectWorkPane() {
        return selectWorkPane;
    }

    private void saveConfig() {
        if (configInfo == null) {
            return;
        }

        var scene = getScene();
        configInfo.setWindowRectangle(getWindowIdentifier(), getX(), getY(), scene.getWidth(), scene.getHeight());
    }

    private Scene getScene() {
        return stage.getScene();
    }

    private double getX() {
        return stage.getX();
    }

    private double getY() {
        return stage.getY();
    }

    static String getWindowIdentifier() {
        return "selectworkdialog";
    }
}
