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
public class GitCheckoutOursTheirsSelectionSituation implements Situation {

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
                case "D":
                    break;
                default:
                    return false;
            }
            switch (t.indexStatusProperty().get()) {
                case "A":
                case "U":
                case "D":
                    return true;
                default:
                    return false;
            }
        }
    };

    public GitCheckoutOursTheirsSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
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
