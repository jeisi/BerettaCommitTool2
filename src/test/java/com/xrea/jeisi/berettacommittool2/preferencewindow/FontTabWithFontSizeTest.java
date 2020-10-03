/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
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
public class FontTabWithFontSizeTest {

    private Stage myStage;

    public FontTabWithFontSizeTest() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        configInfo.setProgram("WinMergeU", "c:/Programs Files/WinMerge/WinMergeU.exe");
        configInfo.setFontSize("12");

        TabPane tabPane = new TabPane(new FontTab(stage, configInfo));
        Scene scene = new Scene(tabPane);
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }

    @Test
    // ConfigInfo で FontSize が指定されていれば、デフォルトでその FontSize が選択されている状態になる。
    public void test(FxRobot robot) throws InterruptedException {
        ListView<String> listView = robot.lookup("#FontTabFontSizeListView").queryAs(ListView.class);
        assertEquals("12", listView.getSelectionModel().getSelectedItem());
    }
}
