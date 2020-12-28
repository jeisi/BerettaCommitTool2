/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import javafx.scene.control.MultipleSelectionModel;

/**
 *
 * @author jeisi
 */
public class DeleteIndexLockSelectionSituation extends SingleSelectionSituation<RepositoryData> {

    public DeleteIndexLockSelectionSituation(MultipleSelectionModel<RepositoryData> selectionModel) {
        super(selectionModel);
    }

    @Override
    public boolean isValid() {
        if(!super.isValid()) {
            return false;
        }
        
        return selectionModel.getSelectedItem().isLocking();
    }
}
