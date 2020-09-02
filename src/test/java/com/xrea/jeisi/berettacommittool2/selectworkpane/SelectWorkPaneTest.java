/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import static com.xrea.jeisi.berettacommittool2.JTestUtility.waitForRunLater;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.joining;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
//import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class SelectWorkPaneTest {

    private SelectWorkPane app;
    private String selectedDirectory;

    @Start
    public void start(Stage stage) {
        app = new SelectWorkPane(stage);
        Scene scene = new Scene(app.build(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testAddDirectory_ComboBoxの要素数が0の時の状態チェック(FxRobot robot) {
        // backSplitMenuButton is unenabled.
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        assertThat(backSplitMenuButton.isDisable()).isEqualTo(true);
    }

    @Test
    public void testAddDirectory_ComboBoxに追加された時の挙動(FxRobot robot) {
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");

        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/Downloads]");

        // 追加されたアイテムは選択された状態になる。
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Downloads");

        // ディレクトリが 1 つしか登録されていれば SplitMenuButton は disable。
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        assertThat(backSplitMenuButton.isDisable()).isEqualTo(true);

    }

    @Test
    public void testAddDirectory_ディレクトリを2つ追加した場合(FxRobot robot) throws InterruptedException {
        //var comboBox = app.comboBox;
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#selectDirectoryButton");

        // 最後に追加されたアイテムが選択された状態になる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Controls");

        // ComboBox の要素はソートされる。
        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/Controls, /home/jeisi/Downloads]");

        // ディレクトが 2 つ以上登録されていれば、SplitMenuButton は enabled になる。
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        assertThat(backSplitMenuButton.isDisable()).isEqualTo(false);

        // SplitMenuButton のプルダウンメニューは追加された順（ソートされない）
        String actual = backSplitMenuButton.getItems().stream().map(x -> x.getText()).collect(joining(", ", "[", "]"));
        assertThat(actual).isEqualTo("[/home/jeisi/Downloads]");

        // ComboBox から選んだ時に、イベントハンドラが登録されていれば呼び出される。
        selectedDirectory = null;
        app.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                selectedDirectory = ((ComboBox<String>) e.getSource()).getValue();
            }
        });
        Platform.runLater(() -> comboBox.getSelectionModel().select("/home/jeisi/Downloads"));
        waitForRunLater();
        assertThat(selectedDirectory).isEqualTo("/home/jeisi/Downloads");
    }

    @Test
    public void testAddDirectory_ディレクトリを3つ追加した場合(FxRobot robot) {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Binaries")));
        robot.clickOn("#selectDirectoryButton");

        // SplitMenuButton のプルダウンメニューは追加された順（ソートされない）
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        String actual = backSplitMenuButton.getItems().stream().map(x -> x.getText()).collect(joining(", ", "[", "]"));
        assertThat(actual).isEqualTo("[/home/jeisi/Controls, /home/jeisi/Downloads]");
    }

    @Test
    public void testAddDirectory_ComboBoxの要素のソートはCaseInsensitive(FxRobot robot) {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/bin")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#selectDirectoryButton");

        // ComboBox の要素は case insensitive でソートされる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/bin, /home/jeisi/Controls]");
    }

    @Test
    public void testAddDirectory_同じディレクトリを連続して追加した場合(FxRobot robot) {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");
        robot.clickOn("#selectDirectoryButton");

        // ComboBox の要素は１つのみ
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/Downloads]");
    }

    @Test
    public void testAddDirectory_既に登録されているディレクトリを追加した場合(FxRobot robot) {
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Controls")));
        robot.clickOn("#selectDirectoryButton");
        app.setDirectoryChooserFactory(() -> new DirectoryChooserBridgeMock(new File("/home/jeisi/Downloads")));
        robot.clickOn("#selectDirectoryButton");

        // 追加されたアイテムは選択された状態になる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Downloads");

        // ComboBox の要素はソートされる。
        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/Controls, /home/jeisi/Downloads]");

        // SplitMenuButton のプルダウンメニューは追加された順（ソートされない、ダブリはなし）
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        String actual = backSplitMenuButton.getItems().stream().map(x -> x.getText()).collect(joining(", ", "[", "]"));
        assertThat(actual).isEqualTo("[/home/jeisi/Controls]");
    }

    @Test
    public void testSetDirectoryHistory(FxRobot robot) throws InterruptedException {
        List<String> directoryHistory = new ArrayList<>();
        directoryHistory.add("/home/jeisi/Downloads");
        directoryHistory.add("/home/jeisi/Controls");
        app.setDirectoryHistory(directoryHistory);
        waitForRunLater();

        // 最後に追加されたアイテムが選択された状態になる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Controls");

        // ComboBox の要素はソートされる。
        assertThat(comboBox.getItems().toString()).isEqualTo("[/home/jeisi/Controls, /home/jeisi/Downloads]");

        // ディレクトが 2 つ以上登録されていれば、SplitMenuButton は enabled になる。
        MenuButton backSplitMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        assertThat(backSplitMenuButton.isDisable()).isEqualTo(false);

        // SplitMenuButton のプルダウンメニューは追加された順（ソートされない）
        String actual = backSplitMenuButton.getItems().stream().map(x -> x.getText()).collect(joining(", ", "[", "]"));
        assertThat(actual).isEqualTo("[/home/jeisi/Downloads]");
    }

    @Test
    // ComboBox から要素を選択した時は、履歴も変更される。
    public void testSelect(FxRobot robot) throws InterruptedException {
        List<String> directoryHistory = new ArrayList<>();
        directoryHistory.add("/home/jeisi/Downloads");
        directoryHistory.add("/home/jeisi/Controls");
        app.setDirectoryHistory(directoryHistory);
        waitForRunLater();

        // 最後に追加されたアイテムが選択された状態になる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Controls");

        // 'Downloads' を選択。
        selectGivenComboBoxItem(robot, comboBox, "/home/jeisi/Downloads");
        //robot.comboBox.getSelectionModel().select(1);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Downloads");

        // 履歴も変更される。
        Assertions.assertEquals(app.getDirectoryHistory().toString(), "[/home/jeisi/Controls, /home/jeisi/Downloads]");
    }

    @Test
    // 戻るボタンから要素を選択した時は、それが ComboBox に反映される。
    public void testBackMenuButton(FxRobot robot) throws InterruptedException {
        List<String> directoryHistory = new ArrayList<>();
        directoryHistory.add("/home/jeisi/Downloads");
        directoryHistory.add("/home/jeisi/Controls");
        app.setDirectoryHistory(directoryHistory);
        waitForRunLater();
        //while(app.getCurrentDirectory() != "");

        // 最後に追加されたアイテムが選択された状態になる。
        ComboBox<String> comboBox = robot.lookup("#directoryComboBox").queryAs(ComboBox.class);
        assertThat(comboBox.getValue()).isEqualTo("/home/jeisi/Controls");

        MenuButton backMenuButton = robot.lookup("#backSplitMenuButton").queryAs(MenuButton.class);
        selectGivenMenuButtonItem(robot, backMenuButton, "/home/jeisi/Downloads");

        Assertions.assertEquals("/home/jeisi/Downloads", comboBox.getValue());
    }

    private void selectGivenComboBoxItem(FxRobot robot, final ComboBox<String> combo, final String item) {
        final int index = combo.getItems().indexOf(item);
        final int indexSel = combo.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            fail("The item " + item + " is not in the combo box " + combo);
        }

        robot.clickOn(combo);

        if (index > indexSel) {
            for (int i = indexSel; i < index; i++) {
                robot.clickOn(combo).type(KeyCode.DOWN);
            }
        } else if (index < indexSel) {
            for (int i = indexSel; i > index; i--) {
                robot.clickOn(combo).type(KeyCode.UP);
            }
        }

        robot.clickOn(combo).type(KeyCode.ENTER);
    }

    private void selectGivenMenuButtonItem(FxRobot robot, final MenuButton menuButton, final String item) {
        robot.clickOn(menuButton);
        for (int i = 0; i < menuButton.getItems().size(); ++i) {
            if (menuButton.getItems().get(i).getText().equals(item)) {
                robot.clickOn("#" + menuButton.getItems().get(i).getId());
                return;
            }
        }
    }
}
