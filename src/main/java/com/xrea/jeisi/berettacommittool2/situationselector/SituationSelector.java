/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.MenuItem;

/**
 *
 * @author jeisi
 */
public class SituationSelector {
    private Situation situation;
    private final List<MenuItem> items = new ArrayList<>();
    
    public SituationSelector() {
    }
    
    public void setSituation(Situation situation) {
        this.situation = situation;        
    }
    
    public List<MenuItem> getItems() {
        return items;
    }
    
    public void update() {
        //System.out.println("SituationSelector.update()");
        boolean isValid = situation.isValid();
        items.forEach(item -> item.setDisable(!isValid));
    }
}
