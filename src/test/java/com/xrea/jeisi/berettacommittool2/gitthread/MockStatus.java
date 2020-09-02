/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.IndexDiff.StageState;
import org.eclipse.jgit.lib.ObjectId;

/**
 *
 * @author jeisi
 */
public class MockStatus extends org.eclipse.jgit.api.Status {

    private Set<String> added = new HashSet<>();
    private Set<String> changed = new HashSet<>();
    private Set<String> missing = new HashSet<>();
    private Set<String> modified = new HashSet<>();
    private Set<String> untracked = new HashSet<>();
    private Map<String, StageState> conflictingStagaState = new HashMap<>();

    public MockStatus() throws IOException {
        super(new IndexDiff(null, (ObjectId) null, null));
    }

    @Override
    public Set<String> getAdded() {
        //System.out.println("MockStatus.getAdded():" + added.toString());
        return added;
    }

    public void setAdded(Set<String> added) {
        this.added = added;
    }

    @Override
    public Set<String> getChanged() {
        return changed;
    }

    public void setChanged(Set<String> changed) {
        this.changed = changed;
    }
    
    @Override
    public Set<String> getModified() {
        return modified;
    }
    
    public void setModified(Set<String> modified) {
        this.modified = modified;
    }

    @Override
    public Set<String> getMissing() {
        return missing;
    }

    @Override
    public Set<String> getUntracked() {
        return untracked;
    }
    
    void setUntracked(Set<String> untracked) {
        this.untracked = untracked;
    }

    @Override
    public Map<String, StageState> getConflictingStageState() {
        return conflictingStagaState;
    }

    public void setConflictingStageState(Map<String, StageState> conflictingStagaState) {
        this.conflictingStagaState = conflictingStagaState;
    }
}
