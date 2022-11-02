/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.basegitpane;

/**
 *
 * @author jeisi
 */
public interface RefreshListener {

    public void clearAll();
    public void refreshAll();
    public void refreshChecked();
    public void refreshSelected();
}
