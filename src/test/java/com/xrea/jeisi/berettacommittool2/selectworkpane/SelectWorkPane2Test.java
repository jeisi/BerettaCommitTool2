/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
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
public class SelectWorkPane2Test {

    private SelectWorkPane2 app;
    private Stage myStage;
    private ConfigInfo configInfo;

    public SelectWorkPane2Test() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;
        configInfo = new ConfigInfo();
        app = new SelectWorkPane2(stage, configInfo) {
            @Override
            boolean checkExist(String selectedItem) {
                return true;
            }
        };
        Scene scene = new Scene(app.build(), 640, 480);
        stage.setScene(scene);
        app.addEventHandler((e) -> stage.close());
        stage.show();
    }

    @Test
    public void testDefault(FxRobot robot) throws InterruptedException {
        // デフォルトでは OK ボタンは disabled.
        Button okButton = robot.lookup("#SelectWorkPane2OkButton").queryAs(Button.class);
        assertTrue(okButton.isDisable());
    }

    /*
    @Test
    public void testAddDirectory_ディレクトリを一つ追加(FxRobot robot) throws InterruptedException {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#addDirectoryButton");

        ListView<String> listView = robot.lookup("#SelectWorkPane2ListView").queryAs(ListView.class);
        assertThat(listView.getItems().toString()).isEqualTo("[/home/jeisi/Downloads]");

    }
     */
    @Test
    public void testAddDirectory_ディレクトリを２つ追加(FxRobot robot) throws InterruptedException {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#addDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#addDirectoryButton");
        
        // ディレクトリが一つ以上追加されれば OK Button は enabled.
        Button okButton = robot.lookup("#SelectWorkPane2OkButton").queryAs(Button.class);
        assertFalse(okButton.isDisable());

        // sort by newest RadioButton を選択。
        robot.clickOn("#SelectWorkPane2SortByNewestRadioButton");

        // 登録したのと逆順で追加されている。
        ListView<String> listView = robot.lookup("#SelectWorkPane2ListView").queryAs(ListView.class);
        assertThat(listView.getItems().toString()).isEqualTo("[/home/jeisi/Downloads, /home/jeisi/Controls]");

        // alphabetical order RadioButton を選択。
        robot.clickOn("#SelectWorkPane2AlphabeticalOrderRadioButton");

        // アルファベット順の並びになっている
        assertThat(listView.getItems().toString()).isEqualTo("[/home/jeisi/Controls, /home/jeisi/Downloads]");

        // /home/jeisi/Controls を選択
        listView.getSelectionModel().select("/home/jeisi/Controls");

        // OK をクリック
        robot.clickOn("#SelectWorkPane2OkButton");

        // /home/jeisi/Controls が最新になっている
        assertEquals("[/home/jeisi/Controls, /home/jeisi/Downloads]", configInfo.getDirectoryHistory().toString());

    }
}
