/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.targetrepositorypane;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.xmlwriter.LogWriter;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 *
 * @author jeisi
 */
public class TargetRepositoryPane {

    //private ObjectProperty<TargetRepository> targetRepositoryProperty = new SimpleObjectProperty<>();
    private RadioButton selectButton;
    private RadioButton checkButton;
    private ObjectProperty<TargetRepository> targetRepositoryProperty;

    public Parent build() {
        ToggleGroup group = new ToggleGroup();

        selectButton = new RadioButton("Selected");
        selectButton.setId("TargetRepositoryPaneSelectButton");
        selectButton.setToggleGroup(group);
        selectButton.setSelected(true);
        selectButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                LogWriter.writeMessage("TargetRepositoryPane.selectButton.selectedProperty().addListener()", "begin");
                targetRepositoryProperty.set(TargetRepository.SELECTED);
            }
        });

        checkButton = new RadioButton("Checked");
        checkButton.setId("TargetRepositoryPaneCheckButton");
        checkButton.setToggleGroup(group);
        checkButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                LogWriter.writeMessage("TargetRepositoryPane.checkButton.selectedProperty().addListener()", "begin");
                targetRepositoryProperty.set(TargetRepository.CHECKED);
            }
        });

        HBox hbox = new HBox(selectButton, checkButton);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5));
        return hbox;
    }

    public void bind(ObjectProperty<TargetRepository> targetRepositoryProperty) {
        this.targetRepositoryProperty = targetRepositoryProperty;
        switch(this.targetRepositoryProperty.get()) {
            case SELECTED:
                selectButton.setSelected(true);
                break;
            case CHECKED:
                checkButton.setSelected(true);
                break;
            default:
                assert(false);
                break;
        }
    }
}
