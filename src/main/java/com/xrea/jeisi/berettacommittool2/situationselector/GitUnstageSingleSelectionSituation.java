/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import javafx.scene.control.MultipleSelectionModel;

/**
 *
 * @author jeisi
 */
public class GitUnstageSingleSelectionSituation extends GitUnstageSelectionSituation {

    public GitUnstageSingleSelectionSituation(MultipleSelectionModel<GitStatusData> selectionModel) {
        super(selectionModel);
    }

    @Override
    public boolean isValid() {
        if (selectionModel.getSelectedIndices().size() != 1) {
            return false;
        }
        return super.isValid();
    }
}
