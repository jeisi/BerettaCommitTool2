/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;
import javafx.scene.control.MultipleSelectionModel;

/**
 *
 * @author jeisi
 */
public class GitAddSelectionSituation implements Situation {

    private final MultipleSelectionModel<GitStatusData> selectionModel;
    private final static Predicate<GitStatusData> predicate = new GitAddPredicate();

    public GitAddSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        var selectedItems = selectionModel.getSelectedItems();
        if(selectedItems.isEmpty()) {
            return false;
        }
        
        return selectedItems.stream().allMatch(predicate);
    }
}
