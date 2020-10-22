/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Arrays;
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
public class SelectWorkPane2WithConfigInfoTest {

    private SelectWorkPane2 app;
    private Stage myStage;

    public SelectWorkPane2WithConfigInfoTest() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;

        List<String> directoryHistory = new ArrayList<String>();
        directoryHistory.add("/home/jeisi/Downloads");      // 最新のディレクトリが一番先頭に登録される
        directoryHistory.add("/home/jeisi/Controls");

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setDirectoryHistory(directoryHistory);
        configInfo.setString("directoryHistory.sort", "sort by newest");

        app = new SelectWorkPane2(stage, configInfo);
        Scene scene = new Scene(app.build(), 640, 480);
        stage.setScene(scene);
        app.addEventHandler((e) -> stage.close());
        stage.show();
    }

    @Test
    public void testDefault(FxRobot robot) throws InterruptedException {
        ListView<String> listView = robot.lookup("#SelectWorkPane2ListView").queryAs(ListView.class);
        RadioButton sortByNewestRadioButton = robot.lookup("#SelectWorkPane2SortByNewestRadioButton").queryAs(RadioButton.class);

        // ディレクトリが一つ以上追加されているので OK Button は enabled.
        Button okButton = robot.lookup("#SelectWorkPane2OkButton").queryAs(Button.class);
        assertFalse(okButton.isDisable());

        // 登録したのと逆順で追加されている。
        assertThat(listView.getItems().toString()).isEqualTo("[/home/jeisi/Downloads, /home/jeisi/Controls]");

        // 最新のディレクトリが現在選択されているディレクトリのはずなので、それが選択されている。
        assertEquals("/home/jeisi/Downloads", listView.getSelectionModel().getSelectedItem());

        // sortedByNewestRadioButton が選択された状態になっている。
        assertTrue(sortByNewestRadioButton.isSelected());

        while (myStage.isShowing()) {
            Thread.sleep(1000);
        }
    }
}
