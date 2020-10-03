/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitCommitWindowTestCommitMessageHistory {

    private GitCommitWindow app;
    private boolean showing;
    private ConfigInfo configInfo;

    public GitCommitWindowTestCommitMessageHistory() {
    }

    @Start
    public void start(Stage stage) throws IOException, InterruptedException {
        configInfo = new ConfigInfo();
        List<String> commitMessageHistory = new ArrayList<>();
        commitMessageHistory.add("alice");
        commitMessageHistory.add("bob");
        commitMessageHistory.add("carol");
        configInfo.setCommitMessageHistory(commitMessageHistory);

        GitCommitPane.setSummaryLength(3);
        app = new GitCommitWindow(configInfo);
        app.getGitCommitPane().setGitCommandFactory(new GitCommandFactoryImpl());
        app.open();

        app.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                GitThreadMan.closeAll();
                showing = false;
            }
        });
        showing = true;

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

        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        //JTestUtility.waitForRunLater();
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        System.out.println("GitCommitWindowTestCommitMessageHistory.tearDown()");
        if (showing) {
            Platform.runLater(() -> app.close());
            while (showing) {
                Thread.sleep(100);
            }
        }

        while (!GitThreadMan.isClosedAll()) {
            Thread.sleep(100);
        }
        System.out.println("tearDown() end.");
    }

    @Test
    @Disabled
    public void testInitialize(FxRobot robot) throws InterruptedException {
        // デフォルトでは ComboBox は選択されていない状態。
        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        assertEquals(null, summaryComboBox.getValue());

        //while(app.showingProperty().get()) {
        //    Thread.sleep(1000);
        //}
    }

    @Test
    // [Commit] ボタンを押した時に、ComboBox に履歴が追加される。
    public void testCommit(FxRobot robot) throws IOException, InterruptedException {
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        messageTextArea.setText("deny");
        while (!messageTextArea.getText().equals("deny")) {
            Thread.sleep(100);
        }

        robot.clickOn("#GitCommitPaneCommitButton");
        JTestUtility.waitForRunLater();

        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        assertEquals("[den, ali, bob, car]", summaryComboBox.getItems().toString());

        app.setX(151);
        app.setY(213);
        app.setWidth(397);
        app.setHeight(432);
        JTestUtility.waitForRunLater();
        
        robot.push(KeyCode.ALT, KeyCode.F4);
        while (showing) {
            Thread.sleep(100);
        }

        assertEquals("[deny, alice, bob, carol]", configInfo.getCommitMessageHistory().toString());
        assertEquals("{151.0, 213.0, 397.0, 432.0}", configInfo.getWindowRectangle(GitCommitWindow.getWindowIdentifier()).toString());
    }

    @Test
    @Disabled
    // 既に SummaryComboBox にあるリストと同じ内容だった場合は、それが先頭にくる。
    public void testCommit2(FxRobot robot) throws InterruptedException {
        System.out.println("GitCommitWindowTestCommitMessageHistory.testCommit2()");
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        messageTextArea.setText("bob");
        while (!messageTextArea.getText().equals("bob")) {
            Thread.sleep(100);
        }

        robot.clickOn("#GitCommitPaneCommitButton");
        JTestUtility.waitForRunLater();

        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        assertEquals("[bob, ali, car]", summaryComboBox.getItems().toString());
    }

    @Test
    @Disabled
    // 既に SummaryComboBox にあるリストの先頭と同じ内容だった場合は、特に変化なし。
    public void testCommit3(FxRobot robot) throws InterruptedException {
        JTestUtility.waitForRunLater();
        
        System.out.println("GitCommitWindowTestCommitMessageHistory.testCommit3()");
        TextArea messageTextArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        messageTextArea.setText("alice");
        while (!messageTextArea.getText().equals("alice")) {
            Thread.sleep(100);
            System.out.println(".");
        }

        robot.clickOn("#GitCommitPaneCommitButton");
        JTestUtility.waitForRunLater();

        ComboBox<String> summaryComboBox = robot.lookup("#GitCommitPaneSummaryComboBox").queryAs(ComboBox.class);
        assertEquals("[ali, bob, car]", summaryComboBox.getItems().toString());
    }
    
    @Test
    @Disabled
    // index.lock が作成できない時は org.eclipse.jgit.errors.LockFailedException がスローされる。
    public void testCommit_IndexLock() {
        fail();
    }
}
