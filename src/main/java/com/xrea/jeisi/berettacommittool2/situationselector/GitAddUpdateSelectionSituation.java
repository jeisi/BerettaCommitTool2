/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;
import javafx.scene.control.TableView;

/**
 *
 * @author jeisi
 */
public class GitAddUpdateSelectionSituation implements Situation {

    private final TableView<GitStatusData> tableView;
    private final static Predicate<GitStatusData> predicate = new GitAddPredicate();

    public GitAddUpdateSelectionSituation(TableView<GitStatusData> tableView) {
        this.tableView = tableView;
    }

    @Override
    public boolean isValid() {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            return false;
        }

        return items.stream().anyMatch(predicate);
    }
}
