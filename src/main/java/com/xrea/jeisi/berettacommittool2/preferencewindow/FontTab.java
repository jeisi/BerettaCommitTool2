/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class FontTab extends Tab implements BaseTab {

    private final ConfigInfo configInfo;
    private final Window parent;
    private ListView<String> fontSizeListView;

    public FontTab(Window parent, ConfigInfo configInfo) {
        super("Font");
        this.configInfo = configInfo;
        this.parent = parent;
        setClosable(false);
        setContent(build());
    }

    @Override
    public void apply() {
        String fontSize = fontSizeListView.getSelectionModel().getSelectedItems().get(0);
        configInfo.setFontSize(fontSize);
    }

    private Node build() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(10, 5, 10, 5));
        
        gridPane.add(new Label("Size:"), 0, 0);

        String fontSizes[] = {"8", "10", "11", "12", "14", "16", "18", "20", "24", "30", "36", "40", "48", "60", "72"};
        fontSizeListView = new ListView<>(FXCollections.observableArrayList(fontSizes));
        gridPane.add(fontSizeListView, 0, 1);

        return gridPane;
    }
}
