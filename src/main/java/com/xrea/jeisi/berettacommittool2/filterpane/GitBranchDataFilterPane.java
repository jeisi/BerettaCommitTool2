/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;
import javafx.collections.transformation.FilteredList;

/**
 *
 * @author jeisi
 */
public class GitBranchDataFilterPane extends FilterPane {

    private FilteredList<String> filteredList;
    private Predicate<String> predicate;

    public GitBranchDataFilterPane(ConfigInfo configInfo, String identifier) {
        super(configInfo, identifier);
    }

    public void setFilteredList(FilteredList<String> filteredList) {
        this.filteredList = filteredList;
        this.filteredList.setPredicate(predicate);
    }

    @Override
    protected void filterTextFieldOnChange() {
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
            predicate = new StringRegexpPredicate(text, isCaseInsensitive);
        } else {
            predicate = new StringFixedPredicate(text, isCaseInsensitive);
        }
        filteredList.setPredicate(predicate);
    }

}
