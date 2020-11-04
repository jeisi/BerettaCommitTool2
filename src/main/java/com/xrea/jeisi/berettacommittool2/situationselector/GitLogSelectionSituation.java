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
public class GitLogSelectionSituation implements Situation {

    private final MultipleSelectionModel<GitStatusData> selectionModel;
    private final static Predicate<GitStatusData> predicate = (t) -> {
        if (t == null) {
            return false;
        }
        switch (t.workTreeStatusProperty().get()) {
            case "!":
            case "?":
                return false;
            default:
                return true;
        }
    };

    public GitLogSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
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
