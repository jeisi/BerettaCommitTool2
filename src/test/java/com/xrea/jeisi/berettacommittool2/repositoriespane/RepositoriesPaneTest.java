/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import java.util.ArrayList;
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


/**
 *
 * @author jeisi
 */


@ExtendWith(ApplicationExtension.class)
public class RepositoriesPaneTest {

    private RepositoriesPane app;

    public RepositoriesPaneTest() {
    }

    @Start
    public void start(Stage stage) {
        app = new RepositoriesPane();
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, app.build());
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testSetRepositories(FxRobot robot) {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, true}]");
    }

    @Test
    public void testMenu_uncheckAllMenuItem(FxRobot robot) {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#uncheckAllMenuItem", Motion.DIRECT);

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, false}, {beretta/gyp, false}]");

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#checkAllMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, true}]");

    }

    @Test
    public void testMenu_checkSelectionMenuItem(FxRobot robot) {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);
        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        tableView.getSelectionModel().select(0);

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#checkSelectionMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, true}, {beretta/gyp, false}]");

        robot.clickOn("#repositoriesMenu");
        robot.clickOn("#invertCheckedMenuItem", Motion.DIRECT);

        assertThat(tableView.getItems().toString()).isEqualTo("[{beretta, false}, {beretta/gyp, true}]");

    }

    @Test
    public void testMenu_selectAllMenuItem(FxRobot robot) {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        RepositoriesInfo work = new RepositoriesInfo(app.getTableView());
        work.setRepositories(repositories, ".");
        app.setRepositories(work);

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

}
