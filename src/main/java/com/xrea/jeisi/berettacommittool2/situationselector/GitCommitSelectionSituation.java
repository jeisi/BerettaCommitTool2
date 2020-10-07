/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.control.TableView;

/**
 *
 * @author jeisi
 */
public class GitCommitSelectionSituation implements Situation {

    private final TableView<GitStatusData> tableView;
    private final Predicate<GitStatusData> predicate = (t) -> {
        switch (t.indexStatusProperty().get()) {
            case "M":
            case "A":
            case "D":
                return true;
            default:
                return false;
        }
    };
    /*
    private final Predicate<GitStatusData> ngPredicate = (t) -> {
        String indexStatus = t.indexStatusProperty().get();
        String workTreeStatus = t.workTreeStatusProperty().get();

        // ?? の状態なら無視.
        if (indexStatus.equals("?") && workTreeStatus.equals("?")) {
            return false; // OK.
        }

        if (!indexStatus.equals("") && !workTreeStatus.equals("")) {
            return true; // NG!!
        }

        return false; // OK.
    };
    */

    public GitCommitSelectionSituation(TableView<GitStatusData> tableView) {
        this.tableView = tableView;
    }

    @Override
    public boolean isValid() {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            return false;
        }

        /*
        if (items.stream().anyMatch(ngPredicate)) {
            return false;
        }
        */

        boolean ret = items.stream().anyMatch(predicate);
        return ret;
    }

}
