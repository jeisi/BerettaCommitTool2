/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class ConfigInfoColumnWidthTest {

    private TableView<Person> tableView;

    public ConfigInfoColumnWidthTest() {
    }

    @Start
    public void start(Stage stage) {
        tableView = new TableView<>();
        var column0 = new TableColumn<Person, String>("First Name");
        column0.setPrefWidth(83);
        var column1 = new TableColumn<Person, String>("Last Name");
        column1.setPrefWidth(31);
        tableView.getColumns().setAll(column0, column1);

        Scene scene = new Scene(tableView, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test() {
        ConfigInfo configInfo = new ConfigInfo();
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth("main", widths);
        
        List<Double> loadWidths = configInfo.getTableColumnWidth("main");
        assertEquals("[83.0, 31.0]", loadWidths.toString());
    }
}
