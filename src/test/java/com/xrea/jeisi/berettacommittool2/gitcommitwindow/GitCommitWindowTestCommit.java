/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
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
public class GitCommitWindowTestCommit {

    private GitCommitWindow app;

    public GitCommitWindowTestCommit() {
    }

    @Start
    public void start(Stage stage) {
        app = new GitCommitWindow();
        app.open();
    }

    @Test
    // Commit Button をクリックしたら、ウィンドウを閉じる。
    public void testCommit(FxRobot robot) throws InterruptedException {
        JTestUtility.waitForRunLater();
        
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        messageTextArea.setText("deny");
        while (!messageTextArea.getText().equals("deny")) {
            Thread.sleep(100);
        }

        robot.clickOn("#GitCommitPaneCommitButton");
        JTestUtility.waitForRunLater();

        assertFalse(app.isShowing());
    }
}
