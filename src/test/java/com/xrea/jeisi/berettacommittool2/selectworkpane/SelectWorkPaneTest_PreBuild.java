/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.selectworkpane.DirectoryChooserBridgeMock;
import static com.xrea.jeisi.berettacommittool2.JTestUtility.waitForRunLater;
import java.io.File;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class SelectWorkPaneTest_PreBuild {

    private Stage stage;
    private SelectWorkPane app;
    private String selectedDirectory;

    public SelectWorkPaneTest_PreBuild() {
    }

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        app = new SelectWorkPane(stage);
    }

    @Test
    public void testSetOnAction_buildが実行される前にsetOnActionが呼ばれた場合(FxRobot robot) throws InterruptedException {
        selectedDirectory = null;
        app.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                selectedDirectory = ((ComboBox<String>) e.getSource()).getValue();
            }
        });

        Platform.runLater(() -> {
            Scene scene = new Scene(app.build(), 640, 480);
            stage.setScene(scene);
            stage.show();
        });
        waitForRunLater();
        
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#selectDirectoryButton");
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        Platform.runLater(() -> comboBox.getSelectionModel().select("/home/jeisi/Downloads"));
        waitForRunLater();
        assertThat(selectedDirectory).isEqualTo("/home/jeisi/Downloads");
    }
}
