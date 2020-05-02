/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import javafx.scene.control.MultipleSelectionModel;

/**
 *
 * @author jeisi
 * @param <T>
 */
public class MultiSelectionSituation<T> implements Situation {

    private final MultipleSelectionModel<T> selectionModel;

    public MultiSelectionSituation(MultipleSelectionModel<T> selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public boolean isValid() {
        return selectionModel.getSelectedIndices().size() > 0;
    }
}
