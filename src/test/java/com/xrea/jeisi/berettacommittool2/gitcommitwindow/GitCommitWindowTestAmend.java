/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
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
public class GitCommitWindowTestAmend {

    private GitCommitWindow app;

    public GitCommitWindowTestAmend() {
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
    public void test(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ObservableList<RepositoryData> selectedRepositories = FXCollections.observableArrayList();
        var work = new RepositoriesInfo(selectedRepositories);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work/beretta").toString());

        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        JTestUtility.waitForRunLater();

        // setRepositoryDatas() を実行したら amend CheckBox は enabled になる。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertFalse(amendCheckBox.isDisable());

        robot.clickOn("#GitCommitPaneAmendCheckBox");
        JTestUtility.waitForRunLater();
        TextArea textArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        assertEquals("Adding a.txt.\n", textArea.getText());
    }

    @Test
    public void まだコミットされていない状態ではamendチェックボックスは選択できない(FxRobot robot) throws IOException, InterruptedException {
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
        JTestUtility.waitForRunLater();

        // setRepositoryDatas() を実行したけど、amend すべきコミットがないなら amend CheckBox は disabled のまま。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertTrue(amendCheckBox.isDisable());
    }

    @Test
    // 複数のリポジトリを amend する場合。
    public void testAmendRepositories(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAmend.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ObservableList<RepositoryData> selectedRepositories = FXCollections.observableArrayList();
        var work = new RepositoriesInfo(selectedRepositories);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("alice");
        repositories.add("bob");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work").toString());

        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        JTestUtility.waitForRunLater();

        robot.clickOn("#GitCommitPaneAmendCheckBox");
        JTestUtility.waitForRunLater();
        TextArea textArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        assertEquals("Adding a.txt.\n---\nAdding b.txt.\n", textArea.getText());
    }

    @Test
    // 複数のリポジトリのうち少なくとも１つがまだコミットがないときは amend CheckBox は選択不可。
    public void testAmendRepositoriesWithNoCommit(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAmend2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ObservableList<RepositoryData> selectedRepositories = FXCollections.observableArrayList();
        var work = new RepositoriesInfo(selectedRepositories);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("alice");
        repositories.add("bob");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work").toString());

        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        JTestUtility.waitForRunLater();

        // setRepositoryDatas() を実行したけど、amend すべきコミットがないリポジトリが含まれているので amend CheckBox は disabled のまま。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertTrue(amendCheckBox.isDisable());
    }

    @Test
    // amend チェックボックスをチェックしたあと、amend チェックボックスのチェックを外した場合はテキストエリアは空になる。
    public void testAmendCancel(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ObservableList<RepositoryData> selectedRepositories = FXCollections.observableArrayList();
        var work = new RepositoriesInfo(selectedRepositories);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work/beretta").toString());

        app.getGitCommitPane().setRepositoryDatas(work.getChecked());
        JTestUtility.waitForRunLater();

        // setRepositoryDatas() を実行したら amend CheckBox は enabled になる。
        CheckBox amendCheckBox = robot.lookup("#GitCommitPaneAmendCheckBox").queryAs(CheckBox.class);
        assertFalse(amendCheckBox.isDisable());

        robot.clickOn("#GitCommitPaneAmendCheckBox");
        JTestUtility.waitForRunLater();
        TextArea textArea = robot.lookup("#GitCommitPaneMessageTextArea").queryAs(TextArea.class);
        assertEquals("Adding a.txt.\n", textArea.getText());

        robot.clickOn("#GitCommitPaneAmendCheckBox");
        JTestUtility.waitForRunLater();
        assertEquals("", textArea.getText());
    }

    private void waitShowing() throws InterruptedException {
        while (app.showingProperty().get()) {
            Thread.sleep(1000);
        }
    }
}
