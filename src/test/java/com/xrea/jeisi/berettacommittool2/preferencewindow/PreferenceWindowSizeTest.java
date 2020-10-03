/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class PreferenceWindowSizeTest {

    private ConfigInfo configInfo;
    private PreferenceWindow app;

    public PreferenceWindowSizeTest() {
    }

    @Start
    public void start(Stage stage) {
        configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        configInfo.setProgram("WinMergeU", "c:/Programs Files/WinMerge/WinMergeU.exe");
        configInfo.setWindowRectangle(PreferenceWindow.getWindowIdentifier(), 110, 120, 800, 600);

        app = new PreferenceWindow(configInfo);
        app.open();
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> app.close());
    }

    @Test
    // ウィンドウを開く時は ConfigInfo で指定されたサイズ。
    public void testOpenWindowTest() {
        var scene = app.getScene();
        assertEquals(110, app.getX());
        assertEquals(120, app.getY());
        assertEquals(800, scene.getWidth());
        assertEquals(600, scene.getHeight());
    }

    @Test
    // 画面を閉じた時にウィンドウサイズを ConfigInfo に保存。
    public void testSaveWindowTest() throws InterruptedException {
        Platform.runLater(() -> {
            app.setX(130);
            app.setY(140);
            app.setWidth(700);
            app.setHeight(500);
            //app.close();
        });

        while (app.getScene().getWidth() != 700) {
            Thread.sleep(1000);
        }
        Platform.runLater(() -> app.close());
        while (app.isShowing()) {
            Thread.sleep(1000);
        }

        assertEquals("{130.0, 140.0, 700.0, 470.0}", configInfo.getWindowRectangle(PreferenceWindow.getWindowIdentifier()).toString());
    }
}
