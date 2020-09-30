/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitStatusPaneCommitSelectorTest {

    private ConfigInfo configInfo;
    private GitStatusPane app;
    private RepositoriesPane repositoriesPane;
    private Menu statusMenu;

    public GitStatusPaneCommitSelectorTest() {
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

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    // (実際の git レポジトリで) modified なファイルが登録されている場合。
    // A  b.txt
    // A  c.txt
    public void testCommitEnabled(FxRobot robot) throws IOException, InterruptedException {
        XmlWriter.writeStartMethod("GitStatusPaneCommitSelectorTest.testCommitEnabled()");
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, workDir.toString());

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);
        app.setConfigInfo(configInfo);
        app.refreshAll();

        // RepositoriesPane で "." を選択。
        while(app.getRefreshThreadCounter() > 0) {
            Thread.sleep(100);
        }
        repositoryTableView.getSelectionModel().select(0);

        // ステージングされたファイルがあるので "Git commit" MenuItem は enable.
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertFalse(commitMenuItem.isDisable());

        XmlWriter.writeEndMethod();
    }

    @Test
    // ステージに modified なファイルのみの登録されている場合。
    public void testCommit(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("M", "", "update.rb", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);

        // リポジトリが選択されていない時点では "Git commit" MenuItem は disable.
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertTrue(commitMenuItem.isDisable());

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);

        // ステージングされたファイルがあるので "Git commit" MenuItem は enable.
        assertFalse(commitMenuItem.isDisable());
    }

    @Test
    // ステージに modified と untracked のファイルが登録されている場合。
    // (untracked なファイルは無視できるので commit 可)
    public void testCommit2(FxRobot robot) throws InterruptedException {
        //System.out.println("GitStatusPaneSelectorTest.testMultiSelectionSelector()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("M", "", "update.rb", repositoryData));
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);

        // リポジトリが選択されていない時点では "Git commit" MenuItem は disable.
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertTrue(commitMenuItem.isDisable());

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);

        // ステージングされたファイルがあるので "Git commit" MenuItem は enable.
        assertFalse(commitMenuItem.isDisable());
    }

    @Test
    // ステージに unmerged のファイルが登録されている場合。
    public void testCommit3(FxRobot robot) throws InterruptedException {
        //System.out.println("GitStatusPaneSelectorTest.testMultiSelectionSelector()");
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add(".");
        work.setRepositories(repositories, "beretta");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("U", "U", "update.rb", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoriesPane.setRepositories(work);
        app.setUp();
        app.setRepositories(work);

        // リポジトリが選択されていない時点では "Git commit" MenuItem は disable.
        MenuItem commitMenuItem = getMenuItem(statusMenu, "gitStatusCommitMenuItem");
        assertTrue(commitMenuItem.isDisable());

        // RepositoriesPane で "." を選択。
        repositoryTableView.getSelectionModel().select(0);

        // unmerged なファイルが存在する場合は commit 不可。
        assertTrue(commitMenuItem.isDisable());
    }
    
    private MenuItem getMenuItem(Menu menu, String menuItemId) {
        List<MenuItem> menuItems = menu.getItems().stream().filter(item -> menuItemId.equals(item.getId())).collect(Collectors.toList());
        return menuItems.get(0);
    }

}
