/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class AppTest {

    private App app;
    private boolean wait;
    private Stage secondStage;

    @Start
    public void start(Stage stage) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/removeConfigYaml.sh");
        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ConfigInfo configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        app = new App();
        app.setConfigInfo(configInfo);
        app.start(stage);
        /*
        Scene scene = app.buildScene(stage);
        stage.setScene(scene);
        stage.show();
         */
    }

    @Test
    public void testRefreshAll(FxRobot robot) throws IOException, InterruptedException {
        //app.setupRepositoriesInfo();

        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testRefreshAll.sh");
        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        String topDir = Paths.get(userDir, "src/test/resources/work/beretta").toString();
        Platform.runLater(() -> app.setRootDirectory(topDir));
        JTestUtility.waitForRunLater();

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        while (tableView.getItems().get(0).displayNameProperty().get().equals(". [updating...]")) {
            Thread.sleep(1000);
        }

        tableView.getSelectionModel().selectAll();
        JTestUtility.waitForRunLater();

        app.refreshAll(topDir);
        JTestUtility.waitForRunLater();
        while (tableView.getItems().get(0).displayNameProperty().get().equals(". [updating...]")) {
            Thread.sleep(1000);
        }

        // refreshAll() 実行後も、実行前に選択されていた状態が維持されている。
        assertEquals("[{., true}, {gyp, true}, {gyptools, true}]", tableView.getSelectionModel().getSelectedItems().toString());

        // -------------------------------------------------------------------------------
        tableView.getItems().get(0).checkProperty().set(false);

        app.refreshAll(topDir);
        JTestUtility.waitForRunLater();
        while (tableView.getItems().get(1).displayNameProperty().get().equals(". [updating...]")) {
            Thread.sleep(1000);
        }

        // refreshAll() 実行後も、実行前のチェック状態が維持されている。
        assertEquals("[{., false}, {gyp, true}, {gyptools, true}]", tableView.getSelectionModel().getSelectedItems().toString());
    }

    @Test
    public void testConfigInfo(FxRobot robot) throws InterruptedException {
        app.mainStage.setWidth(1000);
        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        tableView.getColumns().get(1).setPrefWidth(100);
        
        //waitShowingStage();
        Thread.sleep(1000);
        app.splitPane.setDividerPosition(0, 0.8);
        JTestUtility.waitForRunLater();
        while(tableView.getColumns().get(1).getWidth() != 100 || Math.abs(0.8 - app.splitPane.getDividerPositions()[0]) > 0.001) {
            Thread.sleep(1000);
            System.out.println(tableView.getColumns().get(1).getWidth());
            System.out.println(app.splitPane.getDividerPositions()[0]);
        }
        
        Platform.runLater(() -> app.mainStage.close());

        wait = true;
        Platform.runLater(() -> {
            secondStage = new Stage();
            secondStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == false && newValue == true) {
                    wait = false;
                }
            });
            app.start(secondStage);
        });

        int nCounter = 0;
        while (wait && ++nCounter < 10) {
            Thread.sleep(1000);
        }

        JTestUtility.waitForRunLater();
        // 閉じた時点の divider の値が ConfigInfo に反映されている。
        assertTrue(Math.abs(0.8 - app.configInfo.getDouble("main.splitpane.divider")) < 0.001);
        // 閉じた時点の divider の値が SplitPane に反映されている
        assertTrue(Math.abs(0.8 - app.splitPane.getDividerPositions()[0]) < 0.001);
        tableView = robot.lookup("#tableView").queryAs(TableView.class);
        assertEquals(100, tableView.getColumns().get(1).getWidth());

        Platform.runLater(() -> secondStage.close());
    }

    private void waitShowingStage() throws InterruptedException {
        int nCounter = 0;
        while (app.mainStage.showingProperty().get() /*&& ++nCounter < 10*/) {
            Thread.sleep(1000);
        }
    }
}
