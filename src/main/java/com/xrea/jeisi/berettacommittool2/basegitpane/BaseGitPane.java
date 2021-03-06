/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.basegitpane;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Parent;
import javafx.scene.control.Menu;

/**
 *
 * @author jeisi
 */
public interface BaseGitPane extends RefreshListener {
    public void close();
    public void saveConfig();
    public void setActive(boolean active);
    public void setRepositories(RepositoriesInfo work);
    public void setUp();
    public Menu buildMenu();
    public Parent build();
    public Parent buildToolBar();
    public String getTitle();
    public ObjectProperty<TargetRepository> targetRepositoryProperty();
}
