/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.filebrowser.FileBrowser;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.situationselector.SingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SituationSelector;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    private ConfigInfo configInfo;
    private ErrorLogWindow errorLogWindow;
    private final SituationSelector singleSelectionSituationSelector = new SituationSelector();

    public void setRepositories(RepositoriesInfo work) {
        datas = work.getDatas();
        tableView.setItems(datas);
    }

    public void setConfig(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public void setErrorLogWindow(ErrorLogWindow errorLogWindow) {
        this.errorLogWindow = errorLogWindow;
    }

    public TableView<RepositoryData> getTableView() {
        return tableView;
    }

    public Parent build() {
        //System.out.println("RepositoriesPane.build()");
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
        tableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Integer> change) {
                updateSituationSelectors();
            }
        });
        tableView.setContextMenu(buildContextMenu());

        singleSelectionSituationSelector.setSituation(new SingleSelectionSituation<>(tableView.getSelectionModel()));

        if (configInfo != null) {
            List<Double> widths = configInfo.getTableColumnWidth(tableView.getId());
            //System.out.println("widths: " + widths.toString());
            if (widths != null) {
                for (int index = 0; index < tableView.getColumns().size() && index < widths.size(); ++index) {
                    tableView.getColumns().get(index).setPrefWidth(widths.get(index));
                }
            }
        }

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

    private ContextMenu buildContextMenu() {
        MenuItem copyFilePathMenuItem = new MenuItem("ファイルのフルパスをコピー");
        copyFilePathMenuItem.setOnAction(eh -> copyFilePathToClipBoard());
        singleSelectionSituationSelector.getEnableMenuItems().add(copyFilePathMenuItem);

        MenuItem openFileManagerMenuItem = createOpenFileManagerMenuItem();

        ContextMenu contextMenu = new ContextMenu(copyFilePathMenuItem, openFileManagerMenuItem);
        return contextMenu;
    }

    private void copyFilePathToClipBoard() {
        if (tableView.getSelectionModel().getSelectedItems().size() != 1) {
            throw new AssertionError("ファイルは一つだけ選択されている時しか使用できません");
        }
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        RepositoryData repositoryData = tableView.getSelectionModel().getSelectedItems().get(0);
        Path path = repositoryData.getPath();
        content.putString(path.normalize().toString());
        clipboard.setContent(content);
    }

    public void saveConfig() {
        if (configInfo == null) {
            return;
        }

        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);
    }

    private void updateSituationSelectors() {
        singleSelectionSituationSelector.update();
    }

    private MenuItem createOpenFileManagerMenuItem() {
        MenuItem openFileManagerMenuItem = new MenuItem("ファイルマネージャを開く");
        openFileManagerMenuItem.setOnAction(eh -> openFileManager());
        boolean isSupported = FileBrowser.getInstance().isSupportedOpen();
        if (!isSupported) {
            openFileManagerMenuItem.setDisable(true);
        } else {
            singleSelectionSituationSelector.getEnableMenuItems().add(openFileManagerMenuItem);
        }
        return openFileManagerMenuItem;
    }

    private void openFileManager() {
        RepositoryData repositoryData = tableView.getSelectionModel().getSelectedItems().get(0);
        Path path = repositoryData.getPath();
        FileBrowser.getInstance().setErrorLogWindow(errorLogWindow).browseDirectory(path);
    }
}
