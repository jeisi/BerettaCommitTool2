/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import javafx.scene.control.ListCell;
import javafx.scene.control.cell.ComboBoxListCell;

/**
 *
 * @author jeisi
 */
public class CommitMessageHistoryCell extends ComboBoxListCell<String> {

    @Override
    public void updateItem(String item, boolean empty) {
        String displayItem = (item != null) ? item.split("\n")[0] : item;
        super.updateItem(displayItem, empty);
    }
}