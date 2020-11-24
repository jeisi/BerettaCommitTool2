/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitsyncbranch;

import com.xrea.jeisi.berettacommittool2.basegitpane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author jeisi
 */
public class GitSyncPane implements BaseGitPane {

    private ConfigInfo configInfo;
    private RepositoriesInfo repositories;
    private TableView tableView;
    private ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.CHECKED);

    public GitSyncPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public String getTitle() {
        return "Sync";
    }

    @Override
    public ObjectProperty<TargetRepository> targetRepositoryProperty() {
        return targetRepository;
    }

    @Override
    public void close() {
    }

    @Override
    public void saveConfig() {
    }

    @Override
    public void setRepositories(RepositoriesInfo work) {
        if (this.repositories != null) {
            throw new RuntimeException("setRepositories() を実行するのは一回だけです。");
        }

        this.repositories = work;
    }

    @Override
    public void setUp() {
    }

    @Override
    public Parent build() {
        tableView = new TableView<>();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        return borderPane;
    }

    @Override
    public Menu buildMenu() {
        Menu menu = new Menu("Sync");
        return menu;
    }

    @Override
    public Parent buildToolBar() {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        return hbox;
    }

    @Override
    public void setActive(boolean active) {
        
    }
    
    @Override
    public void refreshAll() {
    }

    @Override
    public void refreshChecked() {
    }

    @Override
    public void refreshSelected() {
    }

}
