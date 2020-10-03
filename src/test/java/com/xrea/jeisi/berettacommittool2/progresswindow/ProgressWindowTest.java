/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.progresswindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
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
public class ProgressWindowTest {

    private ProgressWindow app;

    public ProgressWindowTest() {
    }

    @Start
    public void start(Stage stage) {
        ConfigInfo configInfo = new ConfigInfo();
        
        app = new ProgressWindow(configInfo);
        app.open();
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> app.close());
    }

    @Test
    public void test(FxRobot robot) throws InterruptedException {
        ProgressModel model = new ProgressModel("プログレス実行テスト", 100);
        app.addProgressModel(model);
        ListView<ProgressModel> listView = robot.lookup("#progressWindowListView").queryAs(ListView.class);

        for (int value = 10; value <= 100; value += 10) {
            Thread.sleep(100);
            model.setCurrentValue(value);
        }
        Thread.sleep(2000);

        // プログレスバーが完了後 1 秒したら、項目は削除される。
        assertEquals(0, listView.getItems().size());

        // 項目数が 0 なら、ProgressWindow は非表示。
        assertFalse(app.isShowing());

        Thread.sleep(1000);

        final ProgressModel model2 = new ProgressModel("プログレス実行テスト2", 50);
        Platform.runLater(() -> {
            app.open();
            app.addProgressModel(model2);
        });
        JTestUtility.waitForRunLater();

        // 一旦 hide になったものを再度表示。
        assertTrue(app.isShowing());

        for (int value = 10; value <= 50; value += 10) {
            Thread.sleep(100);
            model2.setCurrentValue(value);
        }

        Thread.sleep(1000);
        
        //while(true) {
        //    Thread.sleep(1000);
        //}
    }
}
