/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitbranchpane;

import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jeisi
 */
public class GitBranchData {

    private StringProperty currentBranch = new SimpleStringProperty("");
    private List<StringProperty> otherBranches = new ArrayList<>();
    private List<StringProperty> remoteBranches = new ArrayList<>();
    private final RepositoryData repositoryData;
    private final StringProperty emptyBranch = new SimpleStringProperty("");

    public GitBranchData(RepositoryData repositoryData) {
        this.repositoryData = repositoryData;
        //currentBranch.set("");
    }

    public void set(GitBranchData o) {
        currentBranch.set(o.currentBranch.get());

        otherBranches = o.otherBranches;
        remoteBranches = o.remoteBranches;
    }

    public RepositoryData getRepositoryData() {
        return repositoryData;
    }

    public StringProperty currentBranchProperty() {
        return currentBranch;
    }

    public StringProperty branchProperty(String id) {
        if (otherBranches == null) {
            return emptyBranch;
        }

        for (var branch : otherBranches) {
            if (branch.get().equals(id)) {
                return branch;
            }
        }
        
        for (var branch : remoteBranches) {
            if (branch.get().equals(id)) {
                return branch;
            }
        }
        
        return emptyBranch;
    }

    public StringProperty remoteBranchProperty(String id) {
        if (remoteBranches == null) {
            return emptyBranch;
        }

        for (var branch : remoteBranches) {
            if (branch.get().equals(id)) {
                return branch;
            }
        }
        return emptyBranch;
    }

    public List<StringProperty> getOtherBranches() {
        return otherBranches;
    }

    public List<StringProperty> getRemoteBranches() {
        return remoteBranches;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(currentBranch.get());
        builder.append(",{");
        boolean isFirst = true;
        for(var branch : otherBranches) {
            if (!isFirst) {
                builder.append(",");
            } else {
                isFirst = false;
            }
            builder.append(branch.get());
        };
        builder.append("},{");
        isFirst = true;
        for(var branch : remoteBranches) {
            if (!isFirst) {
                builder.append(",");
            } else {
                isFirst = false;
            }
            builder.append(branch.get());
        };
        builder.append("}");
        builder.append("}");
        return builder.toString();
    }
}
