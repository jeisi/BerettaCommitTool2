/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.aggregatedobservablearraylist.AggregatedObservableArrayList;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.List;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author jeisi
 */
public class GitCommitSelectionSituation implements Situation {

    private RepositoriesInfo repositories;
    private TargetRepository targetRepository;

    public GitCommitSelectionSituation(RepositoriesInfo repositories, TargetRepository targetRepository) {
        this.repositories = repositories;
        this.targetRepository = targetRepository;
    }

    @Override
    public boolean isValid() {
        ObservableList<RepositoryData> targetRepositories = (targetRepository == TargetRepository.SELECTED) ? repositories.getSelected() : repositories.getChecked();
        if(targetRepositories.isEmpty()) {
            return false;
        }
        return true;
    }

}
