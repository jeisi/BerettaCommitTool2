/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitbranchpane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class GitBranchPaneTest {

    private ConfigInfo configInfo;
    private GitBranchPane app;
    private RepositoriesPane repositoriesPane;
    private Stage stage;

    public GitBranchPaneTest() {
    }

    @Start
    public void start(Stage stage) {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);

        this.stage = stage;
        app = new GitBranchPane(configInfo);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());

        repositoriesPane = new RepositoriesPane();

        HBox hbox = new HBox();
        hbox.getChildren().addAll(repositoriesPane.build(), app.build());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, hbox);
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                app.close();
            }
        });
        stage.show();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void test(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testBranchOther1.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work");

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        work.setRepositories(repositories, workDir.toString());

        repositoriesPane.setRepositories(work);
        app.setRepositories(work);

        Platform.runLater(() -> app.refreshAll());

        int nRetryCounter = 0;
        TableView<ObjectProperty<GitBranchData>> tableView = robot.lookup("#gitBranchTableView").queryAs(TableView.class);
        while (tableView.getColumns().size() != 5 && ++nRetryCounter < 10) {
            Thread.sleep(1000);
        }

        // beretta    , master,    , other1, remotes/origin/master
        // beretta/gyp, master, god,       , remotes/origin/master
        var column0 = tableView.getColumns().get(0);
        var column1 = tableView.getColumns().get(1);
        var column2 = tableView.getColumns().get(2);
        var column3 = tableView.getColumns().get(3);
        var column4 = tableView.getColumns().get(4);
        var row0 = tableView.getItems().get(0);
        var row1 = tableView.getItems().get(1);
        assertEquals("beretta", column0.getCellObservableValue(row0).getValue());
        assertEquals("master", column1.getCellObservableValue(row0).getValue());
        assertEquals("", column2.getCellObservableValue(row0).getValue());
        assertEquals("other1", column3.getCellObservableValue(row0).getValue());
        assertEquals("remotes/origin/master", column4.getCellObservableValue(row0).getValue());
        assertEquals("beretta/gyp", column0.getCellObservableValue(row1).getValue());
        assertEquals("master", column1.getCellObservableValue(row1).getValue());
        assertEquals("god", column2.getCellObservableValue(row1).getValue());
        assertEquals("", column3.getCellObservableValue(row1).getValue());
        assertEquals("remotes/origin/master", column4.getCellObservableValue(row1).getValue());

        Platform.runLater(() -> app.refreshAll());

        JTestUtility.waitForRunLater();
        nRetryCounter = 0;
        while (tableView.getColumns().size() != 5 && ++nRetryCounter < 10) {
            Thread.sleep(1000);
        }

        // Refresh all 実行時に一旦 current branch 以外の column は削除されてから、追加され元の 4 列に戻る。
        assertEquals(5, tableView.getColumns().size());

        // 2 行目のチェックを外す。
        Platform.runLater(() -> row1.get().getRepositoryData().checkProperty().set(false));

        while (tableView.getItems().size() != 1) {
            Thread.sleep(1000);
        }

        // master, other1, remotes/origin/master
        column0 = tableView.getColumns().get(0);
        column1 = tableView.getColumns().get(1);
        column2 = tableView.getColumns().get(2);
        column3 = tableView.getColumns().get(3);
        assertEquals("beretta", column0.getCellObservableValue(row0).getValue());
        assertEquals("master", column1.getCellObservableValue(row0).getValue());
        assertEquals("other1", column2.getCellObservableValue(row0).getValue());
        assertEquals("remotes/origin/master", column3.getCellObservableValue(row0).getValue());

        while (stage.isShowing()) {
            Thread.sleep(1000);
        }
    }
}
