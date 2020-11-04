/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.App;
import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
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
public class SetUpWizardTest {

    private ConfigInfo configInfo;
    private SetUpWizard app;

    public SetUpWizardTest() {
    }

    @Start
    public void start(Stage stage) throws IOException, InterruptedException {
        configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        List<ProgramInfo> programInfos = new ArrayList<>();
        programInfos.add(new ProgramInfo("git", "git.exe", new String[]{"c:/Program Files/Git/bin/git.exe"}));
        programInfos.add(new ProgramInfo("WinMergeU", "WinMergeU.exe", new String[]{"c:/Program Files/WinMerge/WinMergeU.exe"}));

        SetUpWizard.setDebug(true);
        app = new SetUpWizard(configInfo, programInfos);
        app.exec();
    }

    @Test
    // デフォルト状態のチェック。
    public void testDefault(FxRobot robot) throws InterruptedException {
        XmlWriter.writeStartMethod("SetUpWizardTest.testDefault()");

        // デフォルトでは 'Back' ボタンは Disable.
        Button backButton = robot.lookup("#SetUpWizardBackButton").queryAs(Button.class);
        assertEquals(true, backButton.isDisable());

        // デフォルトでは Next ボタン名は 'Next'.
        Button nextButton = robot.lookup("#SetUpWizardNextButton").queryAs(Button.class);
        assertEquals("Next", nextButton.getText());

//        while (app.isShowing()) {
//            Thread.sleep(1000);
//        }
        XmlWriter.writeEndMethod();
    }

    @Test
    // 一番最後まで来たら Next ボタンのテキストは 'Finish' になる。
    public void testFinish(FxRobot robot) throws InterruptedException {
        XmlWriter.writeStartMethod("SetUpWizardTest.testFinish()");
        
        // Next ボタンをクリック。
        robot.clickOn("#SetUpWizardNextButton");
        JTestUtility.waitForRunLater();

        // 一番最後の項目なので Next ボタンのテキストは 'Finish' になっている。
        Button nextButton = robot.lookup("#SetUpWizardNextButton").queryAs(Button.class);
        JTestUtility.waitForRunLater();
        while(!nextButton.getText().equals("Finish")) {
            System.out.println(nextButton.getText());
            Thread.sleep(1000);
        }
        assertEquals("Finish", nextButton.getText());

        JTestUtility.waitForRunLater();
        // Back ボタンをクリック。
        robot.clickOn("#SetUpWizardBackButton");

        // 一番最後の項目でなくなったので、Next ボタンのテキストは 'Next' に戻っている。
        assertEquals("Next", nextButton.getText());
        
        XmlWriter.writeEndMethod();
    }

    @Test
    // 'Finish' ボタンを押したらダイアログを閉じる
    public void testClose(FxRobot robot) throws InterruptedException {
        // Next ボタンをクリック。
        robot.clickOn("#SetUpWizardNextButton");
        Button nextButton = robot.lookup("#SetUpWizardNextButton").queryAs(Button.class);
        assertEquals("Finish", nextButton.getText());
        // Finish ボタンをクリック
        robot.clickOn("#SetUpWizardNextButton");

        // Finish ボタンをクリックしたらダイアログを閉じる
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            while (app.isShowing()) {
                Thread.sleep(1000);
            }
        });

        // 終了後 ConfigInfo にプログラムのパスが追加されている。
        assertEquals("", configInfo.getProgram("WinMergeU", null));
    }
}
