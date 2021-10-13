/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitcommitwindow.GitCommitWindow;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.Stage;
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
public class ConvertCharSetTest {

    private ConvertCharSetWindow app;

    public ConvertCharSetTest() {
    }

    @Start
    public void start(Stage stage) {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        app = new ConvertCharSetWindow(configInfo);
        app.open();

        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir, "src/test/resources/encode_utf8_with_bom.txt");
        app.getPane().setFile(path.toString());
        
    }

    @Test
    public void test() throws InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir, "src/test/resources/encode_utf8_with_bom.txt");
        app.getPane().setFile(path.toString());

        while (app.isShowing()) {
            Thread.sleep(1000);
        }
    }
}
