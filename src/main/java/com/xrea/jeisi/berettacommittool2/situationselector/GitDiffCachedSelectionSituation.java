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
public class GitDiffCachedSelectionSituation implements Situation {

    protected final MultipleSelectionModel<GitStatusData> selectionModel;
    private final Predicate<GitStatusData> predicate = (t) -> {
        if(t.indexStatusProperty().get().equals("A") && t.workTreeStatusProperty().get().equals("A")) {
            return false;
        }

        switch (t.indexStatusProperty().get()) {
            case "M":
            case "A":
                return true;
            default:
                return false;
        }
    };
    
    public GitDiffCachedSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        if (selectionModel.getSelectedIndices().size() != 1) {
            return false;
        }
        return predicate.test(selectionModel.getSelectedItem());
    }

}
