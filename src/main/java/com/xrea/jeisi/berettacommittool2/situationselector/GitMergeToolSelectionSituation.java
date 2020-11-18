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
public class GitMergeToolSelectionSituation implements Situation {

    private final MultipleSelectionModel<GitStatusData> selectionModel;

    private final static Predicate<GitStatusData> predicate = new Predicate<GitStatusData>() {
        @Override
        public boolean test(GitStatusData t) {
            if (t == null) {
                return false;
            }
            switch (t.workTreeStatusProperty().get()) {
                case "A":
                case "U":
                    break;
                default:
                    return false;
            }
            switch (t.indexStatusProperty().get()) {
                case "A":
                case "U":
                    return true;
                default:
                    return false;
            }
        }
    };
    public GitMergeToolSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        var selectedItems = selectionModel.getSelectedItems();
        if (selectedItems.size() != 1) {
            return false;
        }
        
        return predicate.test(selectedItems.get(0));
    }

}
