/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import javafx.scene.control.MenuItem;

/**
 *
 * @author jeisi
 */
public class HierarchyMenuSelectionSituation implements Situation {

    private final MenuItem[] items;
    
    public HierarchyMenuSelectionSituation(MenuItem... items) {
        this.items = items;
    }
    
    @Override
    public boolean isValid() {
        for(MenuItem item : items) {
            if(!item.isDisable()) {
                return true;
            }
        }
        return false;
    }
    
}
