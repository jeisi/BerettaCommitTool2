/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jeisi
 */
public class GitStatusData {

    private final StringProperty indexStatus;
    private final StringProperty workTreeStatus;
    private final StringProperty fileName;
    private final RepositoryData repositoryData;
    private final StringProperty encoding;

    public GitStatusData(String indexStatus, String workTreeStatus, String file, /*String repository,*/ RepositoryData repositoryData) {
        this.indexStatus = new SimpleStringProperty(indexStatus);
        this.workTreeStatus = new SimpleStringProperty(workTreeStatus);
        this.fileName = new SimpleStringProperty(file);
        this.repositoryData = repositoryData;
        this.encoding = new SimpleStringProperty("");
    }
    
    public StringProperty indexStatusProperty() {
        return indexStatus;
    }
    
    public StringProperty workTreeStatusProperty() {
        return workTreeStatus;
    }
    
    public StringProperty fileNameProperty() {
        return fileName;
    }
    
    public StringProperty encodingProperty() {
        return encoding;
    }
    
    public void setIndexStatus(String indexStatus) {
        this.indexStatus.set(indexStatus);
    }
    
    public void setWorkTreeStatus(String workTreeStatus) {
        this.workTreeStatus.set(workTreeStatus);
    }
    
    public String getIndexStatus() {
        return indexStatus.get();
    }
    
    public String getWorkTreeStatus() {
        return workTreeStatus.get();
    }
    
    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }
    
    public String getFileName() {
        return fileName.get();
    }
    
    public RepositoryData getRepositoryData() {
        return repositoryData;
    }
    
    public void setEncoding(String encoding) {
        this.encoding.set(encoding);
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(indexStatus.get());
        builder.append(", ");
        builder.append(workTreeStatus.get());
        builder.append(", ");
        builder.append(fileName.get());
        builder.append(", ");
        builder.append(repositoryData.nameProperty().get());
        builder.append("}");
        return builder.toString();
    }
}
