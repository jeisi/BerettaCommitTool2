/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import javafx.collections.ObservableList;

/**
 *
 * @author jeisi
 */
public class GitCommitSelectionSituation implements Situation {

    private final RepositoriesInfo repositories;
    private final TargetRepository targetRepository;

    public GitCommitSelectionSituation(RepositoriesInfo repositories, TargetRepository targetRepository) {
        this.repositories = repositories;
        this.targetRepository = targetRepository;
    }

    @Override
    public boolean isValid() {
        ObservableList<RepositoryData> targetRepositories = (targetRepository == TargetRepository.SELECTED) ? repositories.getSelected() : repositories.getChecked();
        return !targetRepositories.isEmpty();
    }

}
