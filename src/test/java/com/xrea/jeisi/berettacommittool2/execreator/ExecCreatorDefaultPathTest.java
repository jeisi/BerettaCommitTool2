/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class ExecCreatorDefaultPathTest {

    private ConfigInfo configInfo;
    private ExeCreator app;

    public ExecCreatorDefaultPathTest() {
    }

    @Start
    public void start(Stage stage) throws IOException, InterruptedException {
        configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        SetUpWizard.setDebug(true);
        app = new ExeCreatorUnix(configInfo);
        app.exec();
    }

    @Test
    // プログラムが候補パスの中にあればダイアログは開かない。
    public void testNotOpen() {
    }
}
