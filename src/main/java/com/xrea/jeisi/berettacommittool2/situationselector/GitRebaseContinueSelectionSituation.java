/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;

/**
 *
 * @author jeisi
 */
public class GitRebaseContinueSelectionSituation implements Situation {

    private final RepositoriesInfo repositories;
    private final ObjectProperty<TargetRepository> targetRepository;

    public GitRebaseContinueSelectionSituation(RepositoriesInfo repositories, ObjectProperty<TargetRepository> targetRepository) {
        this.repositories = repositories;
        this.targetRepository = targetRepository;
    }

    @Override
    public boolean isValid() {
        var repositoryDatas = repositories.getTarget(targetRepository.get());
        if(repositoryDatas.isEmpty()) {
            return false;
        }
        
        // 選択されているリポジトリは全て Rebase 中でないといけない。
        Stream<RepositoryData> stream = repositoryDatas.stream();
        if(!stream.allMatch(r -> r.isRebasing())) {
            return false;
        }
        
        // マージ中のものが残っていてはいけない。
        for(var repositoryData : repositoryDatas) {
            boolean isMerging = repositoryData.getGitStatusDatas().stream().anyMatch(s -> s.getIndexStatus().equals("U"));
            if(isMerging) {
                return false;
            }
        }
                
        // ステージングされているものがあれば OK。
        for(var repositoryData : repositoryDatas) {
            boolean isStaging = repositoryData.getGitStatusDatas().stream().anyMatch(s -> s.getIndexStatus().equals("M") || s.getIndexStatus().equals("A") || s.getIndexStatus().equals("D"));
            if(isStaging) {
                return true;
            }
        }        
        
        return false;
    }

}
