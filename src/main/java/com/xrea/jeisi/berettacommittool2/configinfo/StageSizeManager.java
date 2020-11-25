/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class StageSizeManager {

    //private static Rectangle screenRectangle = new Rectangle();
    private static double x0, x1, y0, y1;

    public static void setUp() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        //double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        for (GraphicsDevice g : gs) {
            //DisplayMode dm = g.getDisplayMode();
            Rectangle r = g.getConfigurations()[0].getBounds();
            if (r.x < x0) {
                x0 = r.x;
            }
            if (x1 < r.x + r.width) {
                x1 = r.x + r.width;
            }
            if (r.y < y0) {
                y0 = r.y;
            }
            if (y1 < r.y + r.height) {
                y1 = r.y + r.height;
            }
        }
    }

    public static Scene build(Stage stage, ConfigInfo configInfo, Parent parent, String identifier, double defaultWidth, double defaultHeight) {
        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(identifier) : null;
        double width, height;
        Scene scene;
        if (windowRectangle != null) {
            double x = windowRectangle.getX();
            double y = windowRectangle.getY();
            if(x < x0) {
                x = x0;
            }
            if(y < y0) {
                y = y0;
            }
            if(x1 < x + windowRectangle.getWidth()) {
                x = x1 - windowRectangle.getWidth();
            }
            if(y1 < y + windowRectangle.getHeight()) {
                y = y1 - windowRectangle.getHeight();
            }
            
            stage.setX(x);
            stage.setY(y);
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            scene = new Scene(parent, width, height);
        } else {
            if (defaultWidth < 0 || defaultHeight < 0) {
                scene = new Scene(parent);
            } else {
                scene = new Scene(parent, defaultWidth, defaultHeight);
            }
        }

        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                configInfo.setWindowRectangle(identifier, stage.getX(), stage.getY(), scene.getWidth(), scene.getHeight());
            }
        });

        return scene;
    }
}
