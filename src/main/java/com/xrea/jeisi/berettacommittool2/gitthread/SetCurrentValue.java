/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressModel;

/**
 *
 * @author jeisi
 */
public class SetCurrentValue implements Runnable {

    private final int currentValue;
    private final ProgressModel progressModel;

    SetCurrentValue(ProgressModel progressModel, int currentValue) {
        this.progressModel = progressModel;
        this.currentValue = currentValue;
    }

    @Override
    public void run() {
        progressModel.setCurrentValue(currentValue);
    }
}
