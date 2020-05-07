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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitStatusPaneSelectorTest {

    private GitStatusPane app;
    private RepositoriesPane repositoriesPane;
    private Menu statusMenu;

    public GitStatusPaneSelectorTest() {
    }

    @Start
    public void start(Stage stage) {
        app = new GitStatusPane();
        MenuBar menuBar = new MenuBar();
        statusMenu = app.buildMenu();
        menuBar.getMenus().add(statusMenu);

        //TableView<RepositoryData> repositoryTableView = new TableView<>();
        repositoriesPane = new RepositoriesPane();

        HBox hbox = new HBox();
        hbox.getChildren().addAll(repositoriesPane.build(), app.build());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, hbox);
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /*
    @Test
    // 一行又は複数行選択されている時に有効になる MenuItem のテスト。
    public void testMultiSelectionSelector(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("", "M", "update.rb", "."));
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", "."));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);

        MenuItem addMenuItem = getMenuItem(statusMenu, "gitStatusAddMenuItem");
        // 何も選択されていない状態では "Git add" MenuItem は Disable。
        assertTrue(addMenuItem.isDisable());

        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        gitStatusTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        // 一行選択されている状態では "Git add" MenuItem は Enable。
        assertFalse(addMenuItem.isDisable());
        
        gitStatusTableView.getSelectionModel().selectAll();
        // 複数行選択されている状態でも "Git add" MenuItem は Enable。
        assertFalse(addMenuItem.isDisable());
        
    }
     */

    @Test
    public void testGitAddSelectionSelector(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("A", "", "project.sln", repositoryData));
        berettaStatus.add(new GitStatusData("", "M", "update.rb", repositoryData));
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        MenuItem addMenuItem = getMenuItem(statusMenu, "gitStatusAddMenuItem");
        // 何も選択されていない状態では "Git add" MenuItem は Disable。
        assertTrue(addMenuItem.isDisable());

        // {, M, update.rb, .} が選択されている状態では "Git add" MenuItem は選択可。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        gitStatusTableView.getSelectionModel().clearAndSelect(1);
        JTestUtility.waitForRunLater();
        assertFalse(addMenuItem.isDisable());

        // {?, ?, gyp.sh, .} が選択されている状態では "Git add" MenuItem は選択可。
        gitStatusTableView.getSelectionModel().clearAndSelect(2);
        JTestUtility.waitForRunLater();
        assertFalse(addMenuItem.isDisable());

        // {A, , project.sln, .} が選択されている状態では "Git add" MenuItem は選択不可。
        gitStatusTableView.getSelectionModel().clearAndSelect(0);
        JTestUtility.waitForRunLater();
        assertTrue(addMenuItem.isDisable());

        // {, M, update.rb, .} と {?, ?, gyp.sh, .} が選択されている状態では "Git add" MenuItem は選択可。
        gitStatusTableView.getSelectionModel().clearSelection();
        gitStatusTableView.getSelectionModel().selectRange(1, 3);
        JTestUtility.waitForRunLater();
        assertFalse(addMenuItem.isDisable());

        // {A, , project.sln, .} と {, M, update.rb, .} が選択されている状態では "Git add" MenuItem は選択不可。
        gitStatusTableView.getSelectionModel().clearSelection();
        gitStatusTableView.getSelectionModel().selectRange(0, 2);
        JTestUtility.waitForRunLater();
        assertTrue(addMenuItem.isDisable());
    }

    private MenuItem getMenuItem(Menu menu, String menuItemId) {
        List<MenuItem> menuItems = menu.getItems().stream().filter(item -> menuItemId.equals(item.getId())).collect(Collectors.toList());
        return menuItems.get(0);
    }
}
