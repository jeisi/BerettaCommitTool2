/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.targetrepositorypane;

import static com.xrea.jeisi.berettacommittool2.JTestUtility.waitForRunLater;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.selectworkpane.SelectWorkPane2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
public class TargetRepositoryPaneTest {

    private TargetRepositoryPane app;
    private Stage myStage;

    public TargetRepositoryPaneTest() {
    }

    @Start
    public void start(Stage stage) {
        myStage = stage;
        app = new TargetRepositoryPane();
        Scene scene = new Scene(app.build(), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test(FxRobot robot) throws InterruptedException {
        RadioButton checkButton = robot.lookup("#TargetRepositoryPaneCheckButton").queryAs(RadioButton.class);
        RadioButton selectButton = robot.lookup("#TargetRepositoryPaneSelectButton").queryAs(RadioButton.class);

        // CHECKED 状態をセットした場合。
        ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.CHECKED);
        app.bind(targetRepository);
        waitForRunLater();
        
        int nRetryCounter = 0;
        while (!checkButton.isSelected() && ++nRetryCounter < 10) {
            Thread.sleep(100);
        }
        assertEquals(true, checkButton.isSelected());
        assertEquals(false, selectButton.isSelected());

        // SELECTED 状態をセットした場合。
        ObjectProperty<TargetRepository> targetRepository2 = new SimpleObjectProperty<>(TargetRepository.SELECTED);
        app.bind(targetRepository2);
        
        nRetryCounter = 0;
        while (!selectButton.isSelected()&& ++nRetryCounter < 10) {
            Thread.sleep(100);
        }
        assertEquals(false, checkButton.isSelected());
        assertEquals(true, selectButton.isSelected());
        assertEquals(TargetRepository.CHECKED, targetRepository.get());
        assertEquals(TargetRepository.SELECTED, targetRepository2.get());

        // Check RadioButton がクリックされた場合。
        checkButton.setSelected(true);
        waitForRunLater();
        assertEquals(true, checkButton.isSelected());
        assertEquals(false, selectButton.isSelected());
        assertEquals(TargetRepository.CHECKED, targetRepository2.get());
        

    }
}
