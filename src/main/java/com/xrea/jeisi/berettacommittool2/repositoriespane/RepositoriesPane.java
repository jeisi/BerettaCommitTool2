/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 * @author jeisi
 */
public class RepositoriesPane {
    private ObservableList<RepositoryData> datas;
    private TableView<RepositoryData> tableView;

    public void setRepositories(RepositoriesInfo work) {
        datas = work.getDatas();
        tableView.setItems(datas);
    }
    
    public TableView<RepositoryData> getTableView() {
        return tableView;
    }

    public Parent build() {
        tableView = new TableView<>();
        tableView.setId("tableView");
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        var checkColumn = new TableColumn<RepositoryData, Boolean>("Chk");
        checkColumn.setPrefWidth(40);
        checkColumn.setCellValueFactory((p) -> p.getValue().checkProperty());
        checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn));
        checkColumn.setEditable(true);
        TableColumn<RepositoryData, String> nameColumn = new TableColumn<>("Repository");
        nameColumn.setPrefWidth(300);
        nameColumn.setCellValueFactory((p) -> p.getValue().displayNameProperty());
        nameColumn.setCellFactory((p) -> new StyleTableCell());

        tableView.getColumns().setAll(checkColumn, nameColumn);

        return tableView;
    }

    public Menu buildMenu() {        
        var checkAllMenuItem = new MenuItem("Check all");
        checkAllMenuItem.setId("checkAllMenuItem");
        checkAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        checkAllMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            datas.forEach((e) -> e.checkProperty().set(true));
        });

        var uncheckAllMenuItem = new MenuItem("Uncheck all");
        uncheckAllMenuItem.setId("uncheckAllMenuItem");
        uncheckAllMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            datas.forEach((e) -> e.checkProperty().set(false));
        });

        var checkSelectionMenuItem = new MenuItem("Check selection");
        checkSelectionMenuItem.setId("checkSelectionMenuItem");
        checkSelectionMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        checkSelectionMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            var selectionModel = tableView.getSelectionModel();
            for (int row = 0; row < datas.size(); ++row) {
                datas.get(row).checkProperty().set(selectionModel.isSelected(row));
            }
        });

        var invertCheckedMenuItem = new MenuItem("Invert checked");
        invertCheckedMenuItem.setId("invertCheckedMenuItem");
        invertCheckedMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            datas.forEach((e) -> {
                boolean current = e.checkProperty().get();
                e.checkProperty().set(!current);
            });
        });

        var selectAllMenuItem = new MenuItem("Select all");
        selectAllMenuItem.setId("selectAllMenuItem");
        selectAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        selectAllMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            tableView.getSelectionModel().selectAll();
        });

        var deselectAllMenuItem = new MenuItem("Deselect all");
        deselectAllMenuItem.setId("deselectAllMenuItem");
        deselectAllMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            tableView.getSelectionModel().clearSelection();
        });

        var invertSelectionMenuItem = new MenuItem("Invert selecton");
        invertSelectionMenuItem.setId("invertSelectionMenuItem");
        invertSelectionMenuItem.addEventHandler(ActionEvent.ACTION, event -> {
            var selectionModel = tableView.getSelectionModel();
            var selected = new boolean[datas.size()];
            for (int index = 0; index < datas.size(); ++index) {
                selected[index] = selectionModel.isSelected(index);
            }
            selectionModel.clearSelection();
            for (int row = 0; row < datas.size(); ++row) {
                if (!selected[row]) {
                    selectionModel.select(row);
                }
            }
        });

        var menu = new Menu("Repositories");
        menu.setId("repositoriesMenu");
        menu.getItems().addAll(checkAllMenuItem, uncheckAllMenuItem, checkSelectionMenuItem, invertCheckedMenuItem,
                selectAllMenuItem, deselectAllMenuItem, invertSelectionMenuItem);
        return menu;
    }
}
