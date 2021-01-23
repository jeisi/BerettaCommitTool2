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
public class GitRestoreResetSelectionSituation implements Situation {

    private final MultipleSelectionModel<GitStatusData> selectionModel;
    private final Predicate<GitStatusData> predicate = (t) -> {
        switch (t.indexStatusProperty().get()) {
            case "M":
            case "A":
            case "R":
            case "D":
                return true;
            default:
                break;
        }
        switch(t.workTreeStatusProperty().get()) {
            case "M":
            case "D":
                return true;
            default:
                break;
        }
        return false;
    };

    public GitRestoreResetSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        var selectedItems = selectionModel.getSelectedItems();
        if (selectedItems.isEmpty()) {
            return false;
        }

        return selectedItems.stream().allMatch(predicate);
    }

}
