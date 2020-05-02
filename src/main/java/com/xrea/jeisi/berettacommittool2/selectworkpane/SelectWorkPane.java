/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.selectworkpane.DirectoryChooserBridge;
import com.xrea.jeisi.berettacommittool2.selectworkpane.DirectoryChooserBridgeImpl;
import com.xrea.jeisi.berettacommittool2.selectworkpane.DirectoryChooserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkPane {

    private final Stage stage;
    private ComboBox comboBox;
    private SplitMenuButton backSplitMenuButton;
    private final ObservableList<String> directoriesList = FXCollections.observableArrayList();
    private EventHandler<ActionEvent> onAction;
    private DirectoryChooserFactory directoryChooserFactory = () -> new DirectoryChooserBridgeImpl();

    public SelectWorkPane(Stage stage) {
        this.stage = stage;
    }

    public void setDirectoryHistory(List<String> directoryHistory) {
        directoriesList.setAll(directoryHistory);
        Platform.runLater(() -> {
            comboBox.setValue(directoryHistory.get(directoryHistory.size() - 1));
            updateBackSplitMenuButtonItems();
        });
    }
    
    public List<String> getDirectoryHistory() {
        return directoriesList;
    }

    void setDirectoryChooserFactory(DirectoryChooserFactory factory) {
        directoryChooserFactory = factory;
    }

    public void setOnAction(EventHandler<ActionEvent> event) {
        if (comboBox != null) {
            comboBox.setOnAction(event);
        } else {
            onAction = event;
        }
    }

    public Parent build() {
        var hbox = new HBox();
        hbox.setSpacing(5);

        Comparator<String> comp = (o1, o2) -> o1.compareToIgnoreCase(o2);
        comboBox = new ComboBox<String>(directoriesList.sorted(comp));
        comboBox.setId("directoryComboBox");
        comboBox.setPrefWidth(400);
        if (onAction != null) {
            comboBox.setOnAction(onAction);
        }

        backSplitMenuButton = new SplitMenuButton();
        backSplitMenuButton.setId("backSplitMenuButton");
        backSplitMenuButton.setText("←");
        backSplitMenuButton.setDisable(true);
        var menuItem0 = new MenuItem("History0");
        var menuItem1 = new MenuItem("History1");
        var menuItem2 = new MenuItem("History2");
        backSplitMenuButton.getItems().addAll(menuItem0, menuItem1, menuItem2);

        var selectDirectoryButton = new Button("Select directory");
        selectDirectoryButton.setId("selectDirectoryButton");
        selectDirectoryButton.addEventHandler(ActionEvent.ACTION, event -> {
            DirectoryChooserBridge chooser = directoryChooserFactory.create();
            File selectedDirectory = chooser.showDialog(stage);
            if (selectedDirectory != null) {
                addDirectory(selectedDirectory);
            }
        });

        hbox.getChildren().addAll(comboBox, backSplitMenuButton, selectDirectoryButton);
        return hbox;
    }

    private void addDirectory(File selectedDirectory) {
        var value = selectedDirectory.toString();
        if (value.equals(comboBox.getValue())) {
            return;
        }

        if (directoriesList.contains(value)) {
            directoriesList.remove(value);
        }
        directoriesList.add(value);
        comboBox.getSelectionModel().select(value);

        updateBackSplitMenuButtonItems();
    }

    private void updateBackSplitMenuButtonItems() {
        ArrayList<MenuItem> historiesList = new ArrayList<>();
        var iterator = directoriesList.listIterator(directoriesList.size());
        if (iterator.hasPrevious()) {
            iterator.previous();
        }
        while (iterator.hasPrevious()) {
            var menuItem = new MenuItem(iterator.previous());
            historiesList.add(menuItem);
            if (historiesList.size() > 10) {
                break;
            }
        }
        backSplitMenuButton.getItems().setAll(FXCollections.observableArrayList(historiesList));
        backSplitMenuButton.setDisable(historiesList.size() < 1);
    }
}
