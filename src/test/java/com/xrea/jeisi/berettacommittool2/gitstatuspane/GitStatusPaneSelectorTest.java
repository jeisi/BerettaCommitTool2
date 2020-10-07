/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
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
        ConfigInfo configInfo = new ConfigInfo();

        app = new GitStatusPane(configInfo);
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

    @Test
    // 一行又は複数行選択されている時に有効になる MenuItem のテスト。
    public void testMultiSelectionSelector(FxRobot robot) throws InterruptedException {
        //System.out.println("GitStatusPaneSelectorTest.testMultiSelectionSelector()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("", "M", "update.rb", repositoryData));
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);

        // デフォルトでは Commit MenuItem は選択不可。
        JTestUtility.waitForRunLater();
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertTrue(commitMenuItem.isDisable());
        // デフォルトでは "Git add -u" MenuItem は選択不可
        MenuItem addUpdateMenuItem = getMenuItem(statusMenu, "gitStatusAddUpdateMenuItem");
        assertTrue(addUpdateMenuItem.isDisable());

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);

        MenuItem addMenuItem = getMenuItem(statusMenu, "gitStatusAddMenuItem");
        // 何も選択されていない状態では "Git add" MenuItem は disable.
        assertTrue(addMenuItem.isDisable());
        // 何も選択されていない状態では "Git add -p" MenuItem は disable.
        MenuItem addPatchMenuItem = getMenuItem(statusMenu, "gitStatusAddpMenuItem");
        assertTrue(addPatchMenuItem.isDisable());
        // 何も選択されていない状態では "Git difftool" MenuItem は disable.
        MenuItem diffMenuItem = getMenuItem(statusMenu, "gitStatusDiffMenuItem");
        assertTrue(diffMenuItem.isDisable());
        // 何も選択されていない状態では "Git difftool --cached" MenuItem は disable.
        MenuItem diffCachedMenuItem = getMenuItem(statusMenu, "gitStatusDiffCachedMenuItem");
        assertTrue(diffCachedMenuItem.isDisable());
        // 何も選択されていないけど ' M' なファイルが存在するので enable.
        assertFalse(addUpdateMenuItem.isDisable());

        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        gitStatusTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        // 一行選択されている状態では "Git add" MenuItem は enable.
        assertFalse(addMenuItem.isDisable());
        // 一行選択されている状態では "Git add -p" MenuItem は enable.
        assertFalse(addPatchMenuItem.isDisable());
        // 一行選択されている状態では "Git difftool" MenuItem は enable.
        assertFalse(diffMenuItem.isDisable());
        // 一行選択されているけどステージングされていないので "Git difftool --cached" MenuItem は disable.
        assertTrue(diffCachedMenuItem.isDisable());

        gitStatusTableView.getSelectionModel().selectAll();
        // 複数行選択されている状態でも "Git add" MenuItem は enable.
        assertFalse(addMenuItem.isDisable());
        // 複数行選択されている状態では "Git add -p" MenuItem は disable.
        assertTrue(addPatchMenuItem.isDisable());
        // 複数行選択されている状態では "Git difftool" MenuItem は disable.
        assertTrue(diffMenuItem.isDisable());

    }

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

    @Test
    // git checkout -- 及び git difftool --cached のテスト。
    public void testGitUnstageSelectionSelector(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("A", "", "project.sln", repositoryData));
        berettaStatus.add(new GitStatusData("M", "", "projmake.bat", repositoryData));
        berettaStatus.add(new GitStatusData("D", "", "title.sln", repositoryData));
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

        MenuItem unstageMenuItem = getMenuItem(statusMenu, "gitStatusUnstageMenuItem");
        MenuItem diffCachedMenuItem = getMenuItem(statusMenu, "gitStatusDiffCachedMenuItem");
        // 何も選択されていない状態では "Git reset HEAD <file>..." MenuItem は Disable。
        assertTrue(unstageMenuItem.isDisable());

        // 何も選択されていない状態でもステージングされているファイルがあれば "Git commit" MenuItem は選択可。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertEquals(5, gitStatusTableView.getItems().size());
        assertFalse(commitMenuItem.isDisable());

        // {A, , project.sln, .} が選択されている状態
        gitStatusTableView.getSelectionModel().clearAndSelect(0);
        JTestUtility.waitForRunLater();
        // "Git reset HEAD <file>..." MenuItem は選択可
        assertFalse(unstageMenuItem.isDisable());
        // "Git diff --cached" MenuItem は選択可
        assertFalse(diffCachedMenuItem.isDisable());

        // {D, , title.sln, .} が選択されている状態では "Git reset HEAD <file>..." MenuItem は選択可。
        gitStatusTableView.getSelectionModel().clearAndSelect(2);
        JTestUtility.waitForRunLater();
        assertFalse(unstageMenuItem.isDisable());

        // {M, , projmake.bat, .} が選択されている状態では "Git reset HEAD <file>..." MenuItem は選択可。
        gitStatusTableView.getSelectionModel().clearAndSelect(1);
        JTestUtility.waitForRunLater();
        assertFalse(unstageMenuItem.isDisable());

        // {, M, update.rb, .} が選択されている状態では "Git reset HEAD <file>..." MenuItem は選択不可。
        gitStatusTableView.getSelectionModel().clearAndSelect(3);
        JTestUtility.waitForRunLater();
        assertTrue(unstageMenuItem.isDisable());

        // {?, ?, gyp.sh, .} が選択されている状態では "Git reset HEAD <file>..." MenuItem は選択不可。
        gitStatusTableView.getSelectionModel().clearAndSelect(4);
        JTestUtility.waitForRunLater();
        assertTrue(unstageMenuItem.isDisable());

        // {A, , project.sln, .} と {M, , projmake.bat, .} が選択されている状態:
        gitStatusTableView.getSelectionModel().clearSelection();
        gitStatusTableView.getSelectionModel().selectRange(0, 2);
        JTestUtility.waitForRunLater();
        // "Git reset HEAD <file>..." MenuItem は選択可
        assertFalse(unstageMenuItem.isDisable());
        // 複数選択されているので "Git difftool --cached" MenuItem は選択不可
        assertTrue(diffCachedMenuItem.isDisable());

        // {D, , title.sln, .} と {, M, update.rb, .} が選択されている状態では "Git reset HEAD <file>..." MenuItem は選択不可。
        gitStatusTableView.getSelectionModel().clearSelection();
        gitStatusTableView.getSelectionModel().selectRange(3, 5);
        JTestUtility.waitForRunLater();
        assertTrue(unstageMenuItem.isDisable());
    }

    @Test
    // git difftool 及び git difftool --cached のテスト
    public void testGitDiffSelectionSelector(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("M", "M", "project.sln", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        MenuItem diffMenuItem = getMenuItem(statusMenu, "gitStatusDiffMenuItem");
        MenuItem diffCachedMenuItem = getMenuItem(statusMenu, "gitStatusDiffCachedMenuItem");

        // {M, M, project.sln, .} が選択されている状態
        gitStatusTableView.getSelectionModel().clearAndSelect(0);
        JTestUtility.waitForRunLater();
        // "Git diff" MenuItem は選択可
        assertFalse(diffMenuItem.isDisable());
        // "Git diff --cached" MenuItem は選択可
        assertFalse(diffCachedMenuItem.isDisable());
        // "git commit" MenuItem は選択可。
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertFalse(commitMenuItem.isDisable());
    }

    private MenuItem getMenuItem(Menu menu, String menuItemId) {
        for (MenuItem item : menu.getItems()) {
            if (item instanceof Menu) {
                MenuItem subItem = getMenuItem((Menu) item, menuItemId);
                if (subItem != null) {
                    return subItem;
                }
            }
            if (menuItemId.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }
}
