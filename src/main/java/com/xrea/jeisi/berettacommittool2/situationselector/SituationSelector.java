/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;

/**
 *
 * @author jeisi
 */
public class SituationSelector {

    private Situation situation;
    private final List<MenuItem> enableMenuItems = new ArrayList<>();
    private final List<MenuItem> visibleMenuItems = new ArrayList<>();
    private final List<Button> visibleButtons = new ArrayList<>();

    public SituationSelector() {
    }

    public void setSituation(Situation situation) {
        this.situation = situation;
    }

    public List<MenuItem> getEnableMenuItems() {
        return enableMenuItems;
    }

    public List<MenuItem> getVisibleMenuItems() {
        return visibleMenuItems;
    }

    public List<Button> getVisibleButotns() {
        return visibleButtons;
    }

    public void update() {
        updateEnableMenuItems();
        updateVisibleMenuItems();
        updateVisibleButtons();
    }

    private void updateEnableMenuItems() {
        boolean isValid = situation.isValid();
        enableMenuItems.forEach(item -> item.setDisable(!isValid));
    }

    private void updateVisibleMenuItems() {
        boolean isValid = situation.isValid();
        visibleMenuItems.forEach(item -> item.setVisible(isValid));
    }

    private void updateVisibleButtons() {
        boolean isValid = situation.isValid();
        visibleButtons.forEach(item -> item.setVisible(isValid));
        visibleButtons.forEach(item -> item.setManaged(isValid));
    }
}
