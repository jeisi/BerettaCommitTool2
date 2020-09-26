/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class SetUpNode extends VBox {

    private final ProgramInfo program;
    private final TextField textField;
    private final Window parent;

    public SetUpNode(ProgramInfo program, Window parent) {
        this.parent = parent;
        this.program = program;
        
        Label nameLabel = new Label(program.getExe() + " のフルパスを指定してくだい。");

        textField = new TextField();
        textField.setPrefWidth(400);
        Button selectDirectory = new Button("...");
        selectDirectory.setOnAction(eh -> selectFile());
        HBox inputHBox = new HBox(5, textField, selectDirectory);
        HBox.setHgrow(textField, Priority.ALWAYS);

        getChildren().addAll(nameLabel, inputHBox);
        setSpacing(5);
    }
    
    public String getIdentifier() {
        return program.getIdentifier();
    }

    public String getPath() {
        return textField.getText();
    }
    
    private void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select exe file");
        File selectedFile = chooser.showOpenDialog(parent);
        if(selectedFile != null) {
            textField.setText(selectedFile.getAbsolutePath());
        }
    }
}
