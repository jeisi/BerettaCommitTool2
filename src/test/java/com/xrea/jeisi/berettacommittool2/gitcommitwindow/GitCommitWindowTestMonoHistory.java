/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
public class GitCommitWindowTestMonoHistory {

    private GitCommitWindow app;

    public GitCommitWindowTestMonoHistory() {
    }

    @Start
    public void start(Stage stage) {
        List<String> commitMessageHistory = new ArrayList<>();
        commitMessageHistory.add("Alice");

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setCommitMessageHistory(commitMessageHistory);

        app = new GitCommitWindow();
        app.getGitCommitPane().setConfigInfo(configInfo);
        app.open();
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> app.close());
    }

    @Test
    // ConfigInfo.getCommitMessageHistory() の内容を SummaryComboBox に反映されている。
    public void testSummaryComboBox_SetCommitHistory(FxRobot robot) throws InterruptedException {
        JTestUtility.waitForRunLater();
        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);

        // ConfigInfo.getCommitMessageHistory() の内容を SummaryComboBox に反映されている。
        assertEquals("[Alice]", summaryComboBox.getItems().toString());

        // デフォルトでは ComboBox は何も選択されていない。
        assertEquals(null, summaryComboBox.getValue());

        // デフォルトでは amend チェックボックスは選択されていない。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertFalse(amendCheckBox.isSelected());

        // デフォルトでは TextArea の中身は空。
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        assertEquals("", messageTextArea.getText());

        //while (app.showingProperty().get()) {
        //    Thread.sleep(1000);
        //}
    }

    @Test
    // SummaryComboBox のアイテムを選択するとその内容が TextArea に反映される。
    public void testSummaryComboBox_SelectSummaryComboBox(FxRobot robot) throws InterruptedException {
        JTestUtility.waitForRunLater();
        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        Platform.runLater(() -> {
            summaryComboBox.getSelectionModel().select(0);
        });
        JTestUtility.waitForRunLater();

        //waitShowing();

        // SummaryComboBox で Alice が選択された。
        assertEquals("Alice", summaryComboBox.getValue().toString());

        // SummaryComboBox で選択された内容が TextArea に反映される。
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        assertEquals("Alice", messageTextArea.getText());
    }

    private void waitShowing() throws InterruptedException {
        while (app.showingProperty().get()) {
            Thread.sleep(1000);
        }
    }
}
