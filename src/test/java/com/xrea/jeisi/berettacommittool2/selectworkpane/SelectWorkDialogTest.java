/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
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
public class SelectWorkDialogTest {

    private SelectWorkDialog app;
    
    public SelectWorkDialogTest() {
    }

    @Start
    public void start(Stage stage) {
        ConfigInfo configInfo = new ConfigInfo();
        app = new SelectWorkDialog(configInfo);
        app.showAndWait();
    }


    @Test
    public void test() throws InterruptedException {
        while(app.isShowing()) {
            Thread.sleep(1000);
        }
    }
}
