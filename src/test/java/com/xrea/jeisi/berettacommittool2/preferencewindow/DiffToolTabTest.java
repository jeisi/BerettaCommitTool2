/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
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
public class DiffToolTabTest {

    private Stage myStage;
    private DiffToolTab app;
    private ConfigInfo configInfo;

    public DiffToolTabTest() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;

        configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        configInfo.setProgram("WinMergeU", "c:/Programs Files/WinMerge/WinMergeU.exe");
        configInfo.setDiffTool("meld");

        app = new DiffToolTab(stage, configInfo);
        TabPane tabPane = new TabPane(app);
        Scene scene = new Scene(tabPane);
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }

    @Test
    // ConfigInfo で meld が指定されている場合、デフォルトで meld が選択されている。
    public void testLayout(FxRobot robot) throws InterruptedException {
        RadioButton meldRB = robot.lookup("#DiffToolTabMeldRadioButton").queryAs(RadioButton.class);
        assertTrue(meldRB.isSelected());
    }

}
