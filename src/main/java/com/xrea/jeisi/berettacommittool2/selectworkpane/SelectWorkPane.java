/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
//import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkPane {

    private final Stage stage;
    private ComboBox<String> comboBox;
    //private SplitMenuButton backMenuButton;
    private MenuButton backMenuButton;
    private final List<String> directoriesList = new ArrayList<>();
    private final ObservableList<String> comboItems = FXCollections.observableArrayList();
    private EventHandler<ActionEvent> onAction;
    private DirectoryChooserFactory directoryChooserFactory = () -> new DirectoryChooserBridgeImpl();
    private EventHandler<ActionEvent> historyItemOnAction = (event) -> {
        String selectedItem = ((MenuItem)event.getSource()).getText();
        System.out.println("historyItemOnAction: " + selectedItem);
        comboBox.setValue(selectedItem);
    };

    public SelectWorkPane(Stage stage) {
        this.stage = stage;
    }

    public void setDirectoryHistory(List<String> directoryHistory) {
        //directoriesList.setAll(directoryHistory);
        directoriesList.clear();
        directoriesList.addAll(directoryHistory);
        comboItems.setAll(directoryHistory);
        Platform.runLater(() -> {
            comboBox.setValue(directoryHistory.get(directoryHistory.size() - 1));
            updateBackSplitMenuButtonItems();
        });
    }

    public List<String> getDirectoryHistory() {
        return directoriesList;
    }

    public String getCurrentDirectory() {
        return comboBox.getValue();
    }

    void setDirectoryChooserFactory(DirectoryChooserFactory factory) {
        directoryChooserFactory = factory;
    }

    public void setOnAction(EventHandler<ActionEvent> event) {
        onAction = event;
    }

    public Parent build() {
        var hbox = new HBox();
        hbox.setSpacing(5);

        Comparator<String> comp = (o1, o2) -> o1.compareToIgnoreCase(o2);
        comboBox = new ComboBox<String>(comboItems.sorted(comp));
        comboBox.setId("directoryComboBox");
        comboBox.setPrefWidth(400);
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                changeDirectory();
                if (onAction != null) {
                    onAction.handle(t);
                }
            }
        });

        //backSplitMenuButton = new SplitMenuButton();
        backMenuButton = new MenuButton();
        backMenuButton.setId("backSplitMenuButton");
        backMenuButton.setText("←");
        backMenuButton.setDisable(true);
        //var menuItem0 = new MenuItem("History0");
        //var menuItem1 = new MenuItem("History1");
        //var menuItem2 = new MenuItem("History2");
        //backMenuButton.getItems().addAll(menuItem0, menuItem1, menuItem2);

        //var selectDirectoryButton = new Button("Select directory");
        var selectDirectoryButton = new Button("...");
        selectDirectoryButton.setId("selectDirectoryButton");
        selectDirectoryButton.addEventHandler(ActionEvent.ACTION, event -> {
            DirectoryChooserBridge chooser = directoryChooserFactory.create();
            File selectedDirectory = chooser.showDialog(stage);
            if (selectedDirectory != null) {
                addDirectory(selectedDirectory);
            }
        });

        hbox.getChildren().addAll(comboBox, backMenuButton, selectDirectoryButton);
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

        if (!comboItems.contains(value)) {
            comboItems.add(value);
        }
        comboBox.getSelectionModel().select(value);

        updateBackSplitMenuButtonItems();
    }

    private void changeDirectory() {
        var value = comboBox.getValue();

        if (directoriesList.contains(value)) {
            directoriesList.remove(value);
        }
        directoriesList.add(value);

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
            menuItem.setOnAction(historyItemOnAction);
            menuItem.setId(String.format("historiesListMenuItem%d", historiesList.size()));
            historiesList.add(menuItem);
            if (historiesList.size() > 10) {
                break;
            }
        }
        backMenuButton.getItems().setAll(FXCollections.observableArrayList(historiesList));
        backMenuButton.setDisable(historiesList.size() < 1);
    }
}
