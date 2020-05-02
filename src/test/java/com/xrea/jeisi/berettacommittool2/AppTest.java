/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2;

import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
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

    @Start
    public void start(Stage stage) {
        app = new App();
        Scene scene = app.buildScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testRefreshAll(FxRobot robot) throws IOException, InterruptedException {
        app.setupRepositoriesInfo();

        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testRefreshAll.sh");
        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        String topDir = Paths.get(userDir, "src/test/resources/work/beretta").toString();
        app.setRootDirectory(topDir);

        TableView<RepositoryData> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        while (tableView.getItems().get(0).displayNameProperty().get().equals(". [updating...]")) {
            Thread.sleep(1000);
        }

        tableView.getSelectionModel().selectAll();
        JTestUtility.waitForRunLater();
        System.out.println("tableView.getSelectionModel().getSelectedItems(): " + tableView.getSelectionModel().getSelectedItems().toString());

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
}
