/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;

/**
 *
 * @author jeisi
 */
public class ProgramsTab extends Tab implements BaseTab {

    private final ConfigInfo configInfo;
    private final Window parent;
    private final List<Pair<String, TextField>> items = new ArrayList<>();

    public static String getTitle() {
        return "Programs";
    }
    
    public ProgramsTab(Window parent, ConfigInfo configInfo) {
        super(getTitle());
        this.configInfo = configInfo;
        this.parent = parent;
        setClosable(false);
        setContent(build());
    }

    @Override
    public void apply() {
        for(var item : items) {
            configInfo.setProgram(item.getKey(), item.getValue().getText());
        }
    }
    
    private Parent build() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 5, 10, 5));

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        gridPane.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().add(column2);

        var programs = configInfo.getPrograms();
        for (int y = 0; y < programs.size(); ++y) {
            var p = programs.get(y);
            XmlWriter.writeObject("p", p);
            buildRow(gridPane, y, p.getKey(), p.getValue());
        }

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void buildRow(GridPane gridPane, int y, String identifier, String path) {
        gridPane.add(new Label(identifier), 0, y);

        TextField textField = new TextField(path);
        gridPane.add(textField, 1, y);

        Button selectDirectoryButton = new Button("...");
        selectDirectoryButton.setUserData(textField);
        selectDirectoryButton.setOnAction(eh -> selectDirectory((Button) eh.getSource()));
        gridPane.add(selectDirectoryButton, 2, y);
        
        items.add(new Pair(identifier, textField));
    }

    private void selectDirectory(Button button) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select exe file");
        File selectedFile = chooser.showOpenDialog(parent);
        if (selectedFile != null) {
            var textField = (TextField) button.getUserData();
            textField.setText(selectedFile.getAbsolutePath());
        }
    }

}
