/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
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
public class RepositoriesPaneTestEmptyConfigInfo {

    private RepositoriesPane app;
    private Stage stage;

    public RepositoriesPaneTestEmptyConfigInfo() {
    }

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        app = new RepositoriesPane();
        app.setConfig(new ConfigInfo());
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());
        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, app.build());
        Scene scene = new Scene(vbox, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    // ConfigInfo の getTableColumnWidth() が null を返すときは、column のサイズを変更しない。
    public void testTableColumnWidthNull() {
        assertEquals(300, app.getTableView().getColumns().get(1).getWidth());
    }
    
}
