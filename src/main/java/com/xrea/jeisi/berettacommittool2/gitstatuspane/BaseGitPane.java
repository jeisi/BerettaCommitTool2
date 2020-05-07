/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import javafx.scene.Parent;
import javafx.scene.control.Menu;

/**
 *
 * @author jeisi
 */
public interface BaseGitPane {
    public void close();
    public void refreshAll();
    public void refreshChecked();
    public void refreshSelected();
    public void saveConfig();
    public void setConfigInfo(ConfigInfo configInfo);
    public void setRepositories(RepositoriesInfo work);
    public void setUp();
    public String getTitle();
    public Parent build();
    public Menu buildMenu();
}
