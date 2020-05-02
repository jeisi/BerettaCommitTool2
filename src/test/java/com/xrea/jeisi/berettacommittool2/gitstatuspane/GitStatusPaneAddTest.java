/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.gitthread.MockGitAddCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.MockGitStatusCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.MockGitCommandFactory;
import com.xrea.jeisi.berettacommittool2.gitthread.MockStatus;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
import static org.assertj.core.api.Assertions.assertThat;
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
public class GitStatusPaneAddTest {

    private GitStatusPane app;
    private RepositoriesPane repositoriesPane;
    private Menu statusMenu;

    public GitStatusPaneAddTest() {
    }

    @Start
    public void start(Stage stage) {
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
    public void testAdd(FxRobot robot) throws InterruptedException, IOException {
        //System.out.println("testAdd()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        // git add 実行前の状態。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        //assertThat(gitStatusTableView.getItems().get(0).toString()).isEqualTo("{?, ?, gyp.sh, .}");
        assertEquals("[{?, ?, gyp.sh, .}]", gitStatusTableView.getItems().toString());

        MockStatus mockStatus = new MockStatus();
        mockStatus.setAdded(new HashSet<>(Arrays.asList(new String[]{"gyp.sh"})));
        File berettaFile = Paths.get("beretta/.").toFile();
        MockGitStatusCommand mockGitCommand = new MockGitStatusCommand(berettaFile);
        mockGitCommand.setMockStatus(mockStatus);

        MockGitCommandFactory mockGitCommandFactory = new MockGitCommandFactory();
        mockGitCommandFactory.setMockGitStatusCommand(berettaFile, mockGitCommand);
        mockGitCommandFactory.setMockGitAddCommand(berettaFile, new MockGitAddCommand(berettaFile));
        app.setGitCommandFactory(mockGitCommandFactory);

        // git add 実行後、git status の更新内容を反映させる。
        //System.out.println("--- git add 実行後、git status の更新内容を反映させる。 ---");
        gitStatusTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        robot.clickOn("#gitStatusMenu");
        robot.clickOn("#gitStatusAddMenuItem", Motion.DIRECT);
        JTestUtility.waitForRunLater();
        int nTimeOutCounter = 0;
        while (!gitStatusTableView.getItems().get(0).indexStatusProperty().get().equals("A") && ++nTimeOutCounter < 10) {
            WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);
        }
        assertEquals("[{A, , gyp.sh, .}]", gitStatusTableView.getItems().toString());
    }

    @Test
    // git add 実行後の git status で、その行がなくなった時は、その行を削除。
    public void testAdd_DeleteLine(FxRobot robot) throws InterruptedException, IOException {
        System.out.println("testAdd_DeleteLine()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        // git add 実行前の状態。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        //assertThat(gitStatusTableView.getItems().get(0).toString()).isEqualTo("{?, ?, gyp.sh, .}");
        assertEquals("[{?, ?, gyp.sh, .}]", gitStatusTableView.getItems().toString());

        MockStatus mockStatus = new MockStatus();
        mockStatus.setAdded(new HashSet<>());
        File berettaFile = Paths.get("beretta/.").toFile();
        MockGitStatusCommand mockGitCommand = new MockGitStatusCommand(berettaFile);
        mockGitCommand.setMockStatus(mockStatus);

        MockGitCommandFactory mockGitCommandFactory = new MockGitCommandFactory();
        mockGitCommandFactory.setMockGitStatusCommand(berettaFile, mockGitCommand);
        mockGitCommandFactory.setMockGitAddCommand(berettaFile, new MockGitAddCommand(berettaFile));
        app.setGitCommandFactory(mockGitCommandFactory);

        // git add 実行後の git status で行がなくなった場合。
        System.out.println("--- git add 実行後の git status で行がなくなった場合。---");

        gitStatusTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();
        robot.clickOn("#gitStatusMenu");
        robot.clickOn("#gitStatusAddMenuItem", Motion.DIRECT);
        JTestUtility.waitForRunLater();
        int nCounter = 0;
        while (!gitStatusTableView.getItems().toString().equals("[]") && ++nCounter < 10) {
            Thread.sleep(1000);
        }
        assertEquals("[]", gitStatusTableView.getItems().toString());
    }

    @Test
    // git add 実行後の git status で状態が変わった場合に、それに対応してメニューの選択条件も変わる。
    public void testGitAddSelectionSelector_AfterGitStatus(FxRobot robot) throws IOException, InterruptedException {
        System.out.println("testGitAddSelectionSelector_AfterGitStatus()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("", "M", "update.rb", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);
        JTestUtility.waitForRunLater();

        // {, M, update.rb, .} が選択されている状態では "Git add" MenuItem は選択可。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        gitStatusTableView.getSelectionModel().clearAndSelect(0);
        JTestUtility.waitForRunLater();
        MenuItem addMenuItem = getMenuItem(statusMenu, "gitStatusAddMenuItem");
        assertFalse(addMenuItem.isDisable());

        MockStatus gitStatus = new MockStatus();
        Set<String> added = new HashSet<>();
        added.add("update.rb");
        gitStatus.setAdded(added);
        File workDir = repositoryData.getPath().toFile();
        MockGitStatusCommand mockGitStatusCommand = new MockGitStatusCommand(workDir);
        mockGitStatusCommand.setMockStatus(gitStatus);
        MockGitCommandFactory factory = new MockGitCommandFactory();
        factory.setMockGitStatusCommand(workDir, mockGitStatusCommand);
        MockGitAddCommand mockGitAddCommand = new MockGitAddCommand(workDir);
        factory.setMockGitAddCommand(workDir, mockGitAddCommand);
        app.setGitCommandFactory(factory);

        robot.clickOn("#gitStatusMenu");
        robot.clickOn("#gitStatusAddMenuItem", Motion.DIRECT);
        JTestUtility.waitForRunLater();
        int nTimeoutCounter = 0;
        while (!gitStatusTableView.getItems().toString().equals("[{A, , update.rb, .}]") && ++nTimeoutCounter < 10) {
            Thread.sleep(1000);
        }
        assertEquals("[{A, , update.rb, .}]", gitStatusTableView.getItems().toString());
        // git add したことにより状態が変わったので、"Git add" MenuItem は選択不可になる。
        assertTrue(addMenuItem.isDisable());
    }

    private MenuItem getMenuItem(Menu menu, String menuItemId) {
        //System.out.println("GitStatusPaneAddTest.getMenuItem()");
        //System.out.println("menu.getItems(): " + menu.getItems().toString());
        List<MenuItem> menuItems = menu.getItems().stream().filter(item -> menuItemId.equals(item.getId())).collect(Collectors.toList());
        return menuItems.get(0);
    }
}
