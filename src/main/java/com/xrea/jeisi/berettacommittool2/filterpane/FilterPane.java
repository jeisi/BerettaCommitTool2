/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author jeisi
 */
public class FilterPane {

    private boolean enabled;
    private HBox hbox;
    private TextField textField;
    private CheckBox caseInsensitive;
    private CheckBox regexpCheckBox;
    private FilteredList<GitStatusData> filteredList;
    private final ConfigInfo configInfo;
    private final String identifier;
    private Predicate<GitStatusData> predicate;

    public FilterPane(ConfigInfo configInfo, String identifier) {
        this.configInfo = configInfo;
        this.identifier = identifier;
    }

    public HBox build() {
        Label label = new Label("Filter:");

        textField = new TextField();
        //textField.setOnAction(eh -> filterTextFieldOnChange());
        textField.textProperty().addListener((observable, oldValue, newValue) -> filterTextFieldOnChange());

        caseInsensitive = new CheckBox("Case insensitive");
        caseInsensitive.setOnAction(eh -> filterTextFieldOnChange());

        regexpCheckBox = new CheckBox("Regexp");
        regexpCheckBox.setOnAction(eh -> filterTextFieldOnChange());

        hbox = new HBox(label, textField, caseInsensitive, regexpCheckBox);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(5));
        
        enabled = configInfo.getBoolean(identifier + ".filter.enabled");
        hbox.setVisible(enabled);
        hbox.setManaged(enabled);
        caseInsensitive.setSelected(configInfo.getBoolean(identifier + ".filter.caseinsensitive"));
        regexpCheckBox.setSelected(configInfo.getBoolean(identifier + ".filter.regexp"));
        
        return hbox;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        hbox.setVisible(enabled);
        hbox.setManaged(enabled);
        filterTextFieldOnChange();
        
        if(enabled) {
            textField.requestFocus();
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setFilteredList(FilteredList<GitStatusData> filteredList) {
        this.filteredList = filteredList;
        this.filteredList.setPredicate(predicate);
    }

    public void saveConfig() {
        configInfo.setBoolean(identifier + ".filter.enabled", enabled);
        configInfo.setBoolean(identifier + ".filter.caseinsensitive", caseInsensitive.isSelected());
        configInfo.setBoolean(identifier + ".filter.regexp", regexpCheckBox.isSelected());
    }
    
    private void filterTextFieldOnChange() {
        if (!enabled) {
            predicate = null;
            filteredList.setPredicate(null);
            return;
        }

        String text = textField.getText();
        if (text.isEmpty()) {
            predicate = null;
            filteredList.setPredicate(null);
            return;
        }

        boolean isCaseInsensitive = this.caseInsensitive.isSelected();
        if (regexpCheckBox.isSelected()) {
            predicate = new RegexpPredicate(text, isCaseInsensitive);
        } else {
            predicate = new FixedPredicate(text, isCaseInsensitive);
        }
        filteredList.setPredicate(predicate);
    }
}
