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
public class GitAddPatchSelectionSituation implements Situation {

    private final MultipleSelectionModel<GitStatusData> selectionModel;
    private final static Predicate<GitStatusData> predicate = (t) -> {
        if (t == null) {
            return false;
        }
        switch (t.indexStatusProperty().get()) {
            case "U":
                return false;
            default:
                break;
        }
        switch (t.workTreeStatusProperty().get()) {
            case "M":
            case "D":
                return true;
            default:
                return false;
        }
    };

    public GitAddPatchSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        if (selectionModel.getSelectedIndices().size() != 1) {
            return false;
        }
        return predicate.test(selectionModel.getSelectedItems().get(0));
    }
}
