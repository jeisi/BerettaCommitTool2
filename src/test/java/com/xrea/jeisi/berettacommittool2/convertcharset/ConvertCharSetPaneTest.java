/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import java.util.ArrayList;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
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
public class ConvertCharSetPaneTest {

    private Dialog app;

    public ConvertCharSetPaneTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Start
    public void start(Stage stage) {
        ArrayList<String> files = new ArrayList<>();
        files.add("LowLevelFielderPlayer4.cpp");
        files.add("LowLevelFielderPlayer4.h");
        
        app = new ConvertCharSetDialog(files);
        app.show();
    }

    @Test
    public void test(FxRobot robot) throws InterruptedException {
        int nCounter = 0;
        while (app.isShowing() && ++nCounter < 1000) {
            Thread.sleep(1000);
        }
    }
}
