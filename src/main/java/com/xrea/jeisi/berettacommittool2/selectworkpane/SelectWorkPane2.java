/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SelectWorkPane2 {

    private final ConfigInfo configInfo;
    private final Stage parent;
    private final List<EventHandler<ActionEvent>> actionEvents = new ArrayList<>();
    private DirectoryChooserFactory directoryChooserFactory = () -> new DirectoryChooserBridgeImpl();
    private final ObservableList<String> directoriesList;
    private SortedList<String> sortedDirectoriesList;
    private ListView<String> listView;
    //private String sortType;
    private RadioButton sortByNewestRadioButton;
    private RadioButton alphabeticalOrderRadioButton;
    private Button okButton;
    private Optional<ButtonType> result = Optional.empty();
    private String currentDirectory;

    SelectWorkPane2(Stage parent, ConfigInfo configInfo) {
        this.parent = parent;
        this.configInfo = configInfo;

        var directoryHistory = configInfo.getDirectoryHistory();
        if (directoryHistory != null && directoryHistory.size() > 0) {
            directoriesList = FXCollections.observableArrayList(directoryHistory);
        } else {
            directoriesList = FXCollections.observableArrayList();
        }
    }

    public Parent build() {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(buildTop());
        borderPane.setCenter(buildCenter());
        borderPane.setBottom(buildBottom());
        borderPane.setRight(buildRight());

        if (alphabeticalOrderRadioButton.isSelected()) {
            alphabeticalOrderRadioButton.fireEvent(new ActionEvent(alphabeticalOrderRadioButton, parent));
        } else if (sortByNewestRadioButton.isSelected()) {
            sortByNewestRadioButton.fireEvent(new ActionEvent(sortByNewestRadioButton, parent));
        }

        return borderPane;
    }

    public void setDirectoryHistory(List<String> directoryHistory) {
        directoriesList.clear();
        directoriesList.addAll(directoryHistory);
    }

    public void addEventHandler(EventHandler<ActionEvent> actionEvent) {
        actionEvents.add(actionEvent);
    }

    public Optional<ButtonType> getResult() {
        return result;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void requestFocus() {
        listView.requestFocus();
    }

    private Node buildTop() {
        String sortType = configInfo.getString("directoryHistory.sort");

        alphabeticalOrderRadioButton = new RadioButton("alphabetical order");
        alphabeticalOrderRadioButton.setId("SelectWorkPane2AlphabeticalOrderRadioButton");
        alphabeticalOrderRadioButton.setOnAction(eh -> {
            sortedDirectoriesList.setComparator(Comparator.naturalOrder());
            //sortType = ((RadioButton) eh.getSource()).getText();
        });

        sortByNewestRadioButton = new RadioButton("sort by newest");
        sortByNewestRadioButton.setId("SelectWorkPane2SortByNewestRadioButton");
        sortByNewestRadioButton.setOnAction(eh -> {
            sortedDirectoriesList.setComparator(null);
            //sortType = ((RadioButton) eh.getSource()).getText();
        });

        ToggleGroup group = new ToggleGroup();
        alphabeticalOrderRadioButton.setToggleGroup(group);
        sortByNewestRadioButton.setToggleGroup(group);

        if (alphabeticalOrderRadioButton.getText().equals(sortType)) {
            alphabeticalOrderRadioButton.setSelected(true);
        } else if (sortByNewestRadioButton.getText().equals(sortType)) {
            sortByNewestRadioButton.setSelected(true);
        } else {
            alphabeticalOrderRadioButton.setSelected(true);
        }

        HBox hbox = new HBox(alphabeticalOrderRadioButton, sortByNewestRadioButton);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5));
        return hbox;
    }

    private Node buildCenter() {
        sortedDirectoriesList = directoriesList.sorted();
        listView = new ListView(sortedDirectoriesList);
        listView.setId("SelectWorkPane2ListView");
        if (directoriesList.size() > 0) {
            listView.getSelectionModel().select(directoriesList.get(0));
        }
        listView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Integer> change) {
                while (change.next()) {
                    if (change.wasAdded() || change.wasReplaced()) {
                        okButton.setDisable(false);
                    } else if (change.wasRemoved()) {
                        okButton.setDisable(true);
                    }
                }
            }
        });
        listView.setOnMouseClicked(eh -> {
            boolean doubleClicked = eh.getButton().equals(MouseButton.PRIMARY) && eh.getClickCount() == 2;
            if (doubleClicked) {
                ok();
            }
        });
        listView.setOnKeyPressed(eh -> {
            if(eh.getCode() == KeyCode.ENTER) {
                ok();
            }
            if(eh.getCode() == KeyCode.ESCAPE) {
                cancel();
            }
        });
        return listView;
    }

    private Node buildBottom() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(eh -> cancel());
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        okButton = new Button("OK");
        okButton.setDisable(true);
        okButton.setId("SelectWorkPane2OkButton");
        okButton.setDefaultButton(true);
        okButton.setOnAction(eh -> ok());
        ButtonBar.setButtonData(okButton, ButtonBar.ButtonData.OK_DONE);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(cancelButton, okButton);

        BorderPane.setMargin(buttonBar, new Insets(5, 5, 5, 5));
        return buttonBar;
    }

    private Node buildRight() {
        Button addDirectoryButton = new Button("+");
        addDirectoryButton.setId("addDirectoryButton");
        addDirectoryButton.setOnAction(eh -> selectDirectory());
        addDirectoryButton.setStyle("-fx-font-family: monospace");
        
        Button removeDirectoryButton = new Button("-");
        removeDirectoryButton.setId("removeDirectoryButton");
        //removeDirectoryButton.setDisable(true);
        removeDirectoryButton.setOnAction(eh -> removeDirectory());
        removeDirectoryButton.setStyle("-fx-font-family: monospace");
        removeDirectoryButton.disableProperty().bind(Bindings.createBooleanBinding(() -> listView.getSelectionModel().getSelectedIndices().size() == 0, listView.getSelectionModel().selectedIndexProperty()));
        
        VBox vbox = new VBox(addDirectoryButton, removeDirectoryButton);
        vbox.setSpacing(5);
        
        return vbox;
    }

    boolean checkExist(String selectedItem) {
        if (selectedItem != null) {
            if (!Files.exists(Paths.get(selectedItem))) {
                return false;
            }
        }
        return true;
    }

    private void selectDirectory() {
        DirectoryChooserBridge chooser = directoryChooserFactory.create();
        String lastChoosedDirectory = configInfo.getString("directoryHistory.lastChoosedDirectory");
        //XmlWriter.writeObject("lastChoosedDirectory", lastChoosedDirectory);
        if(!lastChoosedDirectory.isEmpty()) {
            Path path = Paths.get(lastChoosedDirectory);
            if(Files.exists(path)) {
                chooser.setInitialDirectory(path.toFile());                            
            }
        }
        File selectedDirectory = chooser.showDialog(parent);
        if (selectedDirectory != null) {
            addDirectory(selectedDirectory);
        }
    }

    private void addDirectory(File selectedDirectoryFile) {
        String selectedDirectory = selectedDirectoryFile.toString();
        if (!directoriesList.contains(selectedDirectory)) {
            directoriesList.add(0, selectedDirectory);
        }

        listView.getSelectionModel().select(selectedDirectory);
        
        configInfo.setString("directoryHistory.lastChoosedDirectory", selectedDirectory);
    }
    
    private void removeDirectory() {
        var selectedItems = listView.getSelectionModel().getSelectedItems();
        //XmlWriter.writeObject("selectedItems", selectedItems);
        selectedItems.forEach((item) -> {
            //XmlWriter.writeObject("listView.getItems()", listView.getItems());
            directoriesList.remove(item);
        });
        listView.getSelectionModel().clearSelection();
    }

    private void cancel() {
        result = Optional.of(ButtonType.CANCEL);
        fireActionEvents(); // ウィンドウを閉じる
    }

    private void ok() {
        if (okButton.isDisable()) {
            return;
        }

        currentDirectory = listView.getSelectionModel().getSelectedItem();
        if (!checkExist(currentDirectory)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, currentDirectory + " ディレクトリは存在しません", ButtonType.CLOSE);
            alert.showAndWait();

            directoriesList.remove(currentDirectory);
            listView.getSelectionModel().clearSelection();
            currentDirectory = null;
            return;
        }

        result = Optional.of(ButtonType.OK);

        directoriesList.remove(currentDirectory);
        directoriesList.add(0, currentDirectory);

        configInfo.setDirectoryHistory(directoriesList);

        if (alphabeticalOrderRadioButton.isSelected()) {
            configInfo.setString("directoryHistory.sort", alphabeticalOrderRadioButton.getText());
        } else if (sortByNewestRadioButton.isSelected()) {
            configInfo.setString("directoryHistory.sort", sortByNewestRadioButton.getText());
        }

        fireActionEvents(); // ウィンドウを閉じる
    }

    private void fireActionEvents() {
        ActionEvent e = new ActionEvent();
        for (var event : actionEvents) {
            event.handle(e);
        }
    }

    void setDirectoryChooserFactory(DirectoryChooserFactory factory) {
        directoryChooserFactory = factory;
    }
}
