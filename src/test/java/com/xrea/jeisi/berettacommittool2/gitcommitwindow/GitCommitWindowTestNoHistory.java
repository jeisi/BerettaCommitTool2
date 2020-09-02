/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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

    @Test
    public void testSetConfigInfo() {
        // 空の ConfigInfo をセットした場合は何もしない。
        app.getGitCommitPane().setConfigInfo(new ConfigInfo());
    }

    @Test
    // コミット履歴がない時にコミットした場合。
    public void testCommit(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ObservableList<RepositoryData> selectedRepositories = FXCollections.observableArrayList();
        var work = new RepositoriesInfo(selectedRepositories);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work/beretta").toString());

        app.setConfigInfo(new ConfigInfo());
        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        app.getGitCommitPane().setGitCommandFactory(new GitCommandFactoryImpl());

        System.out.println("GitCommitWindowTestCommitMessageHistory.testCommit()");
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        messageTextArea.setText("deny");
        while (!messageTextArea.getText().equals("deny")) {
            Thread.sleep(100);
        }

        robot.clickOn("#GitCommitPaneCommitButton");
        JTestUtility.waitForRunLater();

        Thread.sleep(1000);

        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        assertEquals("[deny]", summaryComboBox.getItems().toString());
    }

    private void waitShowing() throws InterruptedException {
        while (app.showingProperty().get()) {
            Thread.sleep(1000);
        }
    }
}
