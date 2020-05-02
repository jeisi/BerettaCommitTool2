/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
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
public class GitCommitWindowTestNoHistory {

    private GitCommitWindow app;

    public GitCommitWindowTestNoHistory() {
    }

    @Start
    public void start(Stage stage) {
        app = new GitCommitWindow();
        app.open();
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> app.close());
    }

    @Test
    public void testLayout() throws InterruptedException {
        JTestUtility.waitForRunLater();
        //waitShowing();
    }

    @Test
    public void testInitial(FxRobot robot) {
        // setRepositoryData() が呼ばれていない状態では amend は unenabled。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertTrue(amendCheckBox.isDisable());
    }

    private void waitShowing() throws InterruptedException {
        while (app.showingProperty().get()) {
            Thread.sleep(1000);
        }
    }
}
