/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class RepositoriesPaneTest {

    private RepositoriesPane app;
    private Stage stage;

    public RepositoriesPaneTest() {
    }

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        app = new RepositoriesPane();
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, app.build());
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
    
    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> stage.close());
    }

    @Test
    public void testSetRepositories(FxRobot robot) throws InterruptedException {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        //assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, true}]");
        Assertions.assertEquals("[{beretta, true}, {beretta/gyp, true}]", tableView.getItems().toString());
    }

    @Test
    public void testMenu_uncheckAllMenuItem(FxRobot robot) throws InterruptedException {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#uncheckAllMenuItem", Motion.DIRECT);

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, false}, {beretta/gyp, false}]");

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#checkAllMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, true}]");

    }

    @Test
    public void testMenu_checkSelectionMenuItem(FxRobot robot) throws InterruptedException {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);
        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        tableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        waitStageShowed();

        //while(stage.showingProperty().get()) {
        //    Thread.sleep(1000);
        //}
        
        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#checkSelectionMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, false}]");

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#invertCheckedMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, false}, {beretta/gyp, true}]");

    }

    @Test
    public void testMenu_selectAllMenuItem(FxRobot robot) throws InterruptedException {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);
        JTestUtility.waitForRunLater();
        waitStageShowed();

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#selectAllMenuItem", Motion.DIRECT);

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        assertThat(tableView.getSelectionModel().getSelectedIndices().toString()).isEqualTo("[0, 1]");

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#deselectAllMenuItem", Motion.DIRECT);

        assertThat(tableView.getSelectionModel().getSelectedIndices().toString()).isEqualTo("[]");

        tableView.getSelectionModel().select(0);
        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#invertSelectionMenuItem", Motion.DIRECT);

        assertThat(tableView.getSelectionModel().getSelectedIndices().toString()).isEqualTo("[1]");

    }

    private void waitStageShowed() throws InterruptedException {
        int nCounter = 0;
        while (!stage.showingProperty().get() && ++nCounter < 10) {
            Thread.sleep(1000);
        }
    }
}
