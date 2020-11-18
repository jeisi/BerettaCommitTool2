/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import java.io.IOException;
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
public class ExeCreatorNoUnregistedProgramTest {

    private ExeCreator app;
    
    public ExeCreatorNoUnregistedProgramTest() {
    }

    @Start
    public void start(Stage stage) throws IOException, InterruptedException, GitConfigException {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/bin/git");
        configInfo.setProgram("WinMergeU", "/c/Program Files/WinMerge/WinMergeU.exe");
        app = new ExeCreatorWin(configInfo);
        app.exec();
    }

    @Test
    // 未登録ファイルがない時は何も表示しない。
    public void test() {
    }
}
