/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;
import org.testfx.util.WaitForAsyncUtils;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitStatusPaneUnstageTest {

    private GitStatusPane app;
    private RepositoriesPane repositoriesPane;
    private Menu statusMenu;
    private Stage stage;

    public GitStatusPaneUnstageTest() {
    }

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        app = new GitStatusPane();
        MenuBar menuBar = new MenuBar();
        statusMenu = app.buildMenu();
        menuBar.getMenus().add(statusMenu);

        repositoriesPane = new RepositoriesPane();

        HBox hbox = new HBox();
        hbox.getChildren().addAll(repositoriesPane.build(), app.build());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, hbox);
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, Paths.get(userDir, "src/test/resources/work/beretta").toString());

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("A", "", "b.txt", repositoryData));
        berettaStatus.add(new GitStatusData("A", "", "c.txt", repositoryData));
        repositoryData.setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        // unstage 実行前の状態。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        //assertThat(gitStatusTableView.getItems().get(0).toString()).isEqualTo("{?, ?, gyp.sh, .}");
        assertEquals("[{A, , b.txt, .}, {A, , c.txt, .}]", gitStatusTableView.getItems().toString());

        gitStatusTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        robot.clickOn("#gitStatusMenu");
        robot.clickOn("#gitStatusUnstageMenuItem", Motion.DIRECT);
        JTestUtility.waitForRunLater();
        
        //while(stage.showingProperty().get()) {
        //    Thread.sleep(1000);
        //}
        
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            while(!gitStatusTableView.getItems().toString().equals("[{?, ?, b.txt, .}, {A, , c.txt, .}]")) {
                Thread.sleep(1000);
            }
        });
        
    }
}
