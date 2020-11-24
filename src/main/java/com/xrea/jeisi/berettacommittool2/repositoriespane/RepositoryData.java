/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.gitbranchpane.GitBranchData;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
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
    private final BooleanProperty check = new SimpleBooleanProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty displayName = new SimpleStringProperty();
    private ObservableList<GitStatusData> gitStatusDatas = null;
    private ObjectProperty<GitBranchData> gitBranchData; // = new SimpleObjectProperty<>();
    private final Path path; 

    public RepositoryData(boolean bCheck, String name, Path path) {
        //this.gitStatusDatas = FXCollections.observableArrayList();
        //this.gitBranchData.set(new GitBranchData(this));
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
        
//    public void setGitStatusDatas(ObservableList<GitStatusData> gitStatusDatas) {
//        this.gitStatusDatas = gitStatusDatas;
//    }

    public void setGitStatusDatas(List<GitStatusData> gitStatusDatas) {
        XmlWriter.writeStartMethod("RepositoryData.setGitStatusDatas()");
        if(this.gitStatusDatas == null) {
            this.gitStatusDatas = FXCollections.observableArrayList();
        }
        this.gitStatusDatas.setAll(gitStatusDatas);
        XmlWriter.writeEndMethod();
    }
    
    public ObjectProperty<GitBranchData> gitBranchDataProperty() {
        return gitBranchData;
    }
        
    public void setGitBranchData(GitBranchData gitBranchData) {
        XmlWriter.writeStartMethod("RepositoryData.setGitBranchData()");
        if(this.gitBranchData == null) {
            this.gitBranchData = new SimpleObjectProperty<>();
        }
        this.gitBranchData.set(gitBranchData);
        XmlWriter.writeObject("gitBranchData", gitBranchData.toString());
        XmlWriter.writeObject("this.gitBranchData", this.gitBranchData.get().toString());
        XmlWriter.writeEndMethod();
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
        builder.append(",");
        if(gitBranchData == null) {
            builder.append("null");
        } else {
            builder.append(gitBranchData.toString());            
        }
        builder.append("}");
        return builder.toString();
    }
}
