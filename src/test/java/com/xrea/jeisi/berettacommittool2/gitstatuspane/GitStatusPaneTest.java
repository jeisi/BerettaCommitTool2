/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitStatusPaneTest {

    private ConfigInfo configInfo;
    private GitStatusPane app;
    private RepositoriesPane repositoriesPane;
    private Stage stage;

    public GitStatusPaneTest() {
    }

    @Start
    public void start(Stage stage) {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);

        this.stage = stage;
        app = new GitStatusPane(configInfo);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());

        //TableView<RepositoryData> repositoryTableView = new TableView<>();
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

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> {
            stage.close();
            GitThreadMan.closeAll();
        });
    }

    @Test
    public void testSetRepositories(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        work.setRepositories(repositories, ".");

        RepositoryData repositoryData = work.getData(0);
        List<GitStatusData> berettaStatus = new ArrayList<>();
        berettaStatus.add(new GitStatusData("", "M", "update.rb", repositoryData));
        berettaStatus.add(new GitStatusData("?", "?", "gyp.sh", repositoryData));
        work.getData(0).setGitStatusDatas(FXCollections.observableArrayList(berettaStatus));

        repositoryData = work.getData(1);
        List<GitStatusData> berettaGypStatus = new ArrayList<>();
        berettaGypStatus.add(new GitStatusData("", "A", "sources_common_all.txt", repositoryData));
        berettaGypStatus.add(new GitStatusData("M", "", "sources_pawapuro_all.txt", repositoryData));
        work.getData(1).setGitStatusDatas(FXCollections.observableArrayList(berettaGypStatus));

        repositoriesPane.setRepositories(work);
        app.setRepositories(work);
        JTestUtility.waitForRunLater();

        // リポジトリ数は 2 つ。
        assertThat(repositoryTableView.getItems().size()).isEqualTo(2);

        // リポジトリが一つも選択されていない状態のときは、GitStatusPane.tableView の行数は 0。
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        assertThat(gitStatusTableView.getItems().size()).isEqualTo(0);

        // リポジトリ beretta が選択された時。
        TableView.TableViewSelectionModel<RepositoryData> repositorySelectionModel = repositoryTableView.getSelectionModel();
        repositorySelectionModel.select(0);
        assertThat(gitStatusTableView.getItems().size()).isEqualTo(2);

        // リポジトリ beretta, beretta/gyp が選択された時。
        repositorySelectionModel.selectAll();
        assertThat(gitStatusTableView.getItems().size()).isEqualTo(4);
        assertThat(gitStatusTableView.getItems().get(0).toString()).isEqualTo("{, M, update.rb, beretta}");
        assertThat(gitStatusTableView.getItems().get(1).toString()).isEqualTo("{?, ?, gyp.sh, beretta}");
        assertThat(gitStatusTableView.getItems().get(2).toString()).isEqualTo("{, A, sources_common_all.txt, beretta/gyp}");
        assertThat(gitStatusTableView.getItems().get(3).toString()).isEqualTo("{M, , sources_pawapuro_all.txt, beretta/gyp}");

        // 実際に表示されている値のチェック。
        assertThat("update.rb").isEqualTo(gitStatusTableView.getColumns().get(2).getCellObservableValue(0).getValue());
    }

    @Test
    // refreshAll() が実行された時に、GitCommand.status() が正しく実行されるかどうか。
    public void testRefreshAll(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testStatusRefreshAll.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        //RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work");

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        //work.setRepositories(repositories, ".");
        work.setRepositories(repositories, workDir.toString());

        repositoriesPane.setRepositories(work);
        app.setRepositories(work);

        Platform.runLater(() -> repositoryTableView.getSelectionModel().selectAll());
        JTestUtility.waitForRunLater();
        app.refreshAll();
        JTestUtility.waitForRunLater();
        TableView<GitStatusData> gitStatusTableView = robot.lookup("#gitStatusTableView").queryAs(TableView.class);
        int timeOutCounter = 0;
        while (gitStatusTableView.getItems().size() != 2 && ++timeOutCounter < 10) {
            Thread.sleep(1000);
        }

        assertThat("[{A, , gyp.sh, beretta}, {A, , sources_common_all.txt, beretta/gyp}]").isEqualTo(gitStatusTableView.getItems().toString());
    }

    @Test
    // git status コマンド実行時に RepositoryNotFoundException 例外が発生した場合は、
    // RepositoriesPane のリポジトリ名を "beretta [error! repository not found: ...]" のようにする。
    public void testError(FxRobot robot) throws InterruptedException {
        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        String userDir = System.getProperty("user.home");
        Path workDir = Paths.get(userDir);
        var work = new RepositoriesInfo(repositoryTableView);
        work.setRepositories(Arrays.asList("."), workDir.toString());
        repositoriesPane.setRepositories(work);
        app.setRepositories(work);

        app.refreshAll();
        while (work.getData(0).displayNameProperty().get().equals(". [updating...]")) {
            Thread.sleep(1000);
        }

//        while (stage.isShowing()) {
//            Thread.sleep(1000);
//        }
        assertThat(". [error! repository not found: /home/jeisi/.]")
                .isEqualTo(work.getData(0).displayNameProperty().get());

    }

    @Test
    public void testUpdating(FxRobot robot) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testStatusUpdating.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir, "create");
        Process process = pb.start();
        int ret = process.waitFor();

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        Path workDir = Paths.get(userDir, "src/test/resources/work");
        var work = new RepositoriesInfo(repositoryTableView);
        work.setRepositories(Arrays.asList("beretta"), workDir.toString());
        repositoriesPane.setRepositories(work);
        app.setRepositories(work);

        app.refreshAll();
        JTestUtility.waitForRunLater();

        // refreshAll() を実行した直後の表示は "beretta [updating...]"
        assertThat("beretta [updating...]").isEqualTo(work.getData(0).displayNameProperty().get());

        while (work.getData(0).displayNameProperty().get().equals("beretta [updating...]")) {
            Thread.sleep(1000);
        }

        // 更新完了後作業ディレクトリがクリーンな時の表示は "beretta"
        assertThat("beretta").isEqualTo(work.getData(0).displayNameProperty().get());

        // 作業ディレクトリに編集済みのファイルを一つ生成
        pb = new ProcessBuilder("bash", bashCommand.toString(), userDir, "update");
        process = pb.start();
        ret = process.waitFor();

        app.refreshAll();
        JTestUtility.waitForRunLater();
        while (work.getData(0).displayNameProperty().get().equals("beretta [updating...]")) {
            Thread.sleep(1000);
        }

        // 更新完了後作業ディレクトリがクリーンでない時の表示は "beretta (1)"
        assertThat("beretta (1)").isEqualTo(work.getData(0).displayNameProperty().get());

    }
}
