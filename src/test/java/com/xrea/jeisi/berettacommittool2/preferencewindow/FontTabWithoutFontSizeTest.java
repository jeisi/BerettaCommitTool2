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
public class FontTabWithoutFontSizeTest {

    private Stage myStage;

    public FontTabWithoutFontSizeTest() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        configInfo.setProgram("WinMergeU", "c:/Programs Files/WinMerge/WinMergeU.exe");

        TabPane tabPane = new TabPane(new FontTab(stage, configInfo));
        Scene scene = new Scene(tabPane);
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }
    
    @Test
    // ConfigInfo で FontSize が指定されていなければ、デフォルトでは何も選択されていない状態になる。
    public void test(FxRobot robot) throws InterruptedException {
        ListView<String> listView = robot.lookup("#FontTabFontSizeListView").queryAs(ListView.class);
        assertEquals("[]", listView.getSelectionModel().getSelectedIndices().toString());
    }
}
