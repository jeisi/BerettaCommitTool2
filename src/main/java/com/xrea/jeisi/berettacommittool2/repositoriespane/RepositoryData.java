/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.gitbranchpane.GitBranchData;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.LogWriter;
import java.nio.file.Path;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jeisi
 */
public class RepositoryData {

    private ObservableList<GitStatusData> gitStatusDatas = null;
    private ObjectProperty<GitBranchData> gitBranchData; // = new SimpleObjectProperty<>();
    private final BooleanProperty check = new SimpleBooleanProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private final Path path;
    private boolean merging = false;
    private boolean reverting = false;
    private boolean cherryPicking = false;
    private boolean rebasing = false;
    private boolean locking = false;
    private boolean isInitializedGitStatusDatas = false;
    private String gitDir;

    public RepositoryData(boolean bCheck, String name, Path path) {
        this.check.set(bCheck);
        this.name.set(name);
        this.path = path;
        this.displayName.set(name);
    }

    public BooleanProperty checkProperty() {
        return check;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public ObservableList<GitStatusData> getGitStatusDatas() {
        return gitStatusDatas;
    }

    public void resetGitStatusDatas() {
        isInitializedGitStatusDatas = false;
    }
    
    public boolean isInitializedGitStatusDatas() {
        return isInitializedGitStatusDatas;
    }

    public void setGitStatusDatas(List<GitStatusData> gitStatusDatas) {
        //LogWriter.writeObject("RepositoryData.setGitStatusDatas()", "gitStatusDatas", gitStatusDatas.toString());
        if (this.gitStatusDatas == null) {
            this.gitStatusDatas = FXCollections.observableArrayList();
        }
        this.gitStatusDatas.setAll(gitStatusDatas);
        isInitializedGitStatusDatas = true;
    }

    public ObjectProperty<GitBranchData> gitBranchDataProperty() {
        return gitBranchData;
    }

    public void setGitBranchData(GitBranchData gitBranchData) {
        if (this.gitBranchData == null) {
            this.gitBranchData = new SimpleObjectProperty<>();
        }
        this.gitBranchData.set(gitBranchData);
    }

    public boolean isMerging() {
        return merging;
    }

    public void setMerging(boolean merging) {
        this.merging = merging;
    }

    public boolean isReverting() {
        return reverting;
    }

    public void setReverting(boolean reverting) {
        this.reverting = reverting;
    }

    public boolean isCherryPicking() {
        return cherryPicking;
    }

    public void setCherryPicking(boolean cherryPicking) {
        this.cherryPicking = cherryPicking;
    }

    public boolean isRebasing() {
        return rebasing;
    }

    public void setRebasing(boolean rebasing) {
        this.rebasing = rebasing;
    }

    public boolean isLocking() {
        return locking;
    }

    public void setLocking(boolean locking) {
        this.locking = locking;
    }

    public void setGitDir(String gitDir) {
        this.gitDir = gitDir;
    }

    public String getGitDir() {
        return gitDir;
    }

    public Path getPath() {
        return path;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(name.get());
        builder.append(", ");
        builder.append(check.get());
        builder.append(", gitStatusDatas=");
        if (gitStatusDatas == null) {
            builder.append("null");
        } else {
            builder.append(gitStatusDatas.toString());
        }
        builder.append(", gitBranchData=");
        if (gitBranchData == null) {
            builder.append("null");
        } else {
            builder.append(gitBranchData.toString());
        }
        builder.append("}");
        return builder.toString();
    }
}
