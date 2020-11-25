/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriesinfo;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author jeisi
 */
public class RepositoriesInfo {

    private ObservableList<RepositoryData> datas;
    private ObservableList<RepositoryData> selectedRepositories;
    private ObservableList<RepositoryData> checkedRepositories;

    public RepositoriesInfo(TableView<RepositoryData> tableView) {
        this(tableView.getSelectionModel().getSelectedItems());
    }

    public RepositoriesInfo(ObservableList<RepositoryData> selectedRepositories) {
        datas = FXCollections.observableArrayList();
        this.selectedRepositories = selectedRepositories;
        checkedRepositories = FXCollections.observableArrayList();
    }

    public ObservableList<RepositoryData> getDatas() {
        return datas;
    }

    public RepositoryData getData(int index) {
        return datas.get(index);
    }

    public ObservableList<RepositoryData> getSelected() {
        return selectedRepositories;
    }

    public ObservableList<RepositoryData> getChecked() {
        return checkedRepositories;
    }

    public ObservableList<RepositoryData> getTarget(TargetRepository targetRepository) {
        return (targetRepository == TargetRepository.SELECTED) ? getSelected() : getChecked();
    }

    public void setRepositories(List<String> repositories, String topDir) {
        List<RepositoryData> rowDatas = new ArrayList<>();
        repositories.forEach((repository) -> {
            var rowData = new RepositoryData(true, repository, Paths.get(topDir, repository));
            rowDatas.add(rowData);
            rowData.checkProperty().addListener((observable, oldValue, newValue) -> {
                updateChecked();
            });
        });
        datas.setAll(rowDatas);

        updateChecked();
    }

    private void updateChecked() {
        List<RepositoryData> newCheckedRepositories
                = datas.stream().filter(e -> e.checkProperty().get()).collect(Collectors.toList());
        checkedRepositories.setAll(newCheckedRepositories);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (datas == null) {
            builder.append("null");
        } else {
            builder.append(datas);
        }
        builder.append(",");
        if (selectedRepositories == null) {
            builder.append("null");
        } else {
            builder.append(selectedRepositories);
        }
        builder.append(",");
        if (checkedRepositories == null) {
            builder.append("null");
        } else {
            builder.append(checkedRepositories);
        }
        return builder.toString();
    }
}
