/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitbranchpane;

import com.xrea.jeisi.berettacommittool2.basegitpane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.filterpane.GitBranchDataFilterPane;
import static com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusPane.setRepositoryDisplayName;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.gitthread.GitBranchCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Parent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author jeisi
 */
public class GitBranchPane implements BaseGitPane {

    private boolean active = false;
    private ObservableList<ObjectProperty<GitBranchData>> branchDatas;
    private RepositoriesInfo repositoriesInfo;
    private TableView<ObjectProperty<GitBranchData>> tableView;
    private ObservableList<String> branchNames;
    private FilteredList<String> filteredBranchNames;
    private final ConfigInfo configInfo;
    private final ErrorLogWindow errorLogWindow;
    private final GitBranchDataFilterPane filterPane;
    private final AtomicInteger refreshThreadCounter = new AtomicInteger();
    private final ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.CHECKED);
    private Menu menu;

    public GitBranchPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.errorLogWindow = new ErrorLogWindow(configInfo);
        this.filterPane = new GitBranchDataFilterPane(configInfo, "gitbranchpane");

        branchNames = FXCollections.observableArrayList();
    }

    @Override
    public String getTitle() {
        return "Branch";
    }

    @Override
    public ObjectProperty<TargetRepository> targetRepositoryProperty() {
        return targetRepository;
    }

    @Override
    public void setRepositories(RepositoriesInfo repositoriesInfo) {
        this.repositoriesInfo = repositoriesInfo;

        ListChangeListener<RepositoryData> changedListener = (change) -> changeTargetRepositories(TargetRepository.CHECKED);
        this.repositoriesInfo.getChecked().addListener(changedListener);

        ListChangeListener<RepositoryData> listener = ll -> changeTargetRepositories(TargetRepository.SELECTED);
        repositoriesInfo.getSelected().addListener(listener);

        changeTargetRepositories(targetRepository.get());
    }

    private void changeTargetRepositories(TargetRepository target) {
        if (!active) {
            return;
        }

        if (target != targetRepository.get()) {
            return;
        }

        ObservableList<RepositoryData> targetRepositories = (target == TargetRepository.SELECTED) ? repositoriesInfo.getSelected() : repositoriesInfo.getChecked();
        branchDatas = FXCollections.observableArrayList();
        targetRepositories.forEach(r -> {
            branchDatas.add(r.gitBranchDataProperty());
        });
        tableView.setItems(branchDatas);
        tableView.getColumns().remove(2, tableView.getColumns().size());
        combineBranch();
        updateSituationSelectors();
    }

    @Override
    public void setActive(boolean active) {
        menu.setVisible(active);

        this.active = active;
        if (!active) {
            return;
        }

        if (repositoriesInfo.getDatas().stream().filter(p -> p.gitBranchDataProperty() == null).count() > 0) {
            refreshAll();
        }
        //changeTargetRepositories(targetRepository.get());

    }

    @Override
    public Parent build() {
        targetRepository.addListener((observable, oldValue, newValue) -> {
            changeTargetRepositories(newValue);
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(buildCenter());
        borderPane.setBottom(buildBottom());
        return borderPane;
    }

    private Parent buildCenter() {
        tableView = new TableView();
        tableView.setId("gitBranchTableView");

        var repositoryTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Repository");
        repositoryTableColumn.setPrefWidth(100);
        repositoryTableColumn.setCellValueFactory(p -> p.getValue().get().getRepositoryData().nameProperty());
        repositoryTableColumn.setId("Repository");
        branchNames.add(repositoryTableColumn.getId());

        var currentBranchTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Current Branch");
        currentBranchTableColumn.setPrefWidth(100);
        currentBranchTableColumn.setCellValueFactory(p -> p.getValue().get().currentBranchProperty());
        currentBranchTableColumn.setId("Current Branch");
        branchNames.add(currentBranchTableColumn.getId());

        tableView.getColumns().addAll(repositoryTableColumn, currentBranchTableColumn);

        if (configInfo != null) {
            for (int index = 0; index < tableView.getColumns().size(); ++index) {
                var column = tableView.getColumns().get(index);
                double width = configInfo.getBranchColumnWidth(tableView.getId(), column.getId());
                column.setPrefWidth(width);
            }
        }

        return tableView;
    }

    private Parent buildBottom() {
        Parent parent = filterPane.build();

        filteredBranchNames = new FilteredList<>(branchNames);
        filteredBranchNames.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                onChangedFileter();
            }
        });
        filterPane.setFilteredList(filteredBranchNames);

        return parent;
    }

    private void onChangedFileter() {
        for (var column : tableView.getColumns()) {
            if ("Repository".equals(column.getId()) || "Current Branch".equals(column.getId())) {
                continue;
            }
            boolean isVisible = filteredBranchNames.contains(column.getId());
            column.setVisible(isVisible);
        }
    }

    @Override
    public Menu buildMenu() {
        CheckMenuItem filterMenuItem = new CheckMenuItem("Filter");
        filterMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        filterMenuItem.setOnAction(eh -> {
            boolean isSelected = ((CheckMenuItem) eh.getSource()).isSelected();
            filterPane.setEnabled(isSelected);
        });
        filterMenuItem.setSelected(filterPane.isEnabled());

        menu = new Menu("Branch");
        menu.getItems().addAll(filterMenuItem);
        menu.setVisible(false);
        return menu;
    }

    @Override
    public Parent buildToolBar() {
        HBox hbox = new HBox();
        //hbox.getChildren().addAll(commitButton, addButton, unstageButton, diffButton, diffCachedButton, gitLogButton, checkoutHeadButton, checkoutTheirsButton, checkoutOursButton, deleteButton);
        hbox.setSpacing(5);
        return hbox;
    }

    @Override
    public void setUp() {
    }

    @Override
    public void saveConfig() {
        HashMap<String, Double> map = new HashMap<>();
        for (var column : tableView.getColumns()) {
            map.put(column.getId(), column.getWidth());
        }
        configInfo.setBranchColumnWidth(tableView.getId(), map);

        filterPane.saveConfig();
    }

    @Override
    public void close() {
        errorLogWindow.close();
    }

    @Override
    public void clearAll() {
        for (RepositoryData repository : repositoriesInfo.getDatas()) {
            GitThread thread = GitThreadMan.get(repository.getPath().toString());
            thread.addCommand(() -> {
                clearRepository(repository);
            });
        }
    }

    private void clearRepository(RepositoryData repository) {
    }

    @Override
    public void refreshAll() {
        XmlWriter.writeStartMethod("GtiBranchPane.refreshAll()");
        refreshCommon(repositoriesInfo.getDatas());
        XmlWriter.writeEndMethod();
    }

    @Override
    public void refreshChecked() {
        refreshCommon(repositoriesInfo.getChecked());
    }

    @Override
    public void refreshSelected() {
        refreshCommon(repositoriesInfo.getSelected());
    }

    private void refreshCommon(ObservableList<RepositoryData> datas) {
        refreshThreadCounter.addAndGet(datas.size());
        datas.forEach((var repositoryData) -> {
            refreshRepository(repositoryData);
        });
    }

    private void refreshRepository(RepositoryData repository) {
        repository.displayNameProperty().set(String.format("%s [updating...]", repository.nameProperty().get()));
        tableView.getColumns().remove(2, tableView.getColumns().size());
        //repository.getGitBranchDatas().clear();
        GitThread thread = GitThreadMan.get(repository.getPath().toString());
        thread.addCommand(() -> {
            GitBranchCommand command = new GitBranchCommand(repository.getPath(), configInfo);
            GitBranchData gitBranchData;
            try {
                gitBranchData = command.exec(repository);
                repository.setGitBranchData(gitBranchData);
                setRepositoryDisplayName(repository);
            } catch (RepositoryNotFoundException ex) {
                Platform.runLater(() -> errorLogWindow.appendException(ex));
                repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getShortMessage()));
                return;
            } catch (IOException | GitConfigException | InterruptedException ex) {
                Platform.runLater(() -> errorLogWindow.appendException(ex));
                repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getMessage()));
                return;
            } finally {
                int remain = refreshThreadCounter.decrementAndGet();
                if (remain == 0) {
                    changeTargetRepositories(targetRepository.get());
                    //combineBranch();
                }
            }
        });
    }

    private void combineBranch() {
        //XmlWriter.writeStartMethod("GitBranchPane.combineBranch()");
        Set<String> otherBranches = new TreeSet<>();
        Set<String> remoteBranches = new TreeSet<>();
        //XmlWriter.writeObject("tableView.getItems()", tableView.getItems().toString());
        tableView.getItems().forEach(item -> {
            //XmlWriter.writeObject("item.get()", item.get().toString());
            item.get().getOtherBranches().forEach(branch -> otherBranches.add(branch.get()));
            item.get().getRemoteBranches().forEach(branch -> remoteBranches.add(branch.get()));
        });
        //XmlWriter.writeObject("otherBranches", otherBranches);

        Platform.runLater(() -> {
            otherBranches.forEach(branch -> addBranchColumn(branch));
            remoteBranches.forEach(branch -> addBranchColumn(branch));
        });
        //XmlWriter.writeEndMethod();
    }

    private void addBranchColumn(String branch) {
        double width = configInfo.getBranchColumnWidth(tableView.getId(), branch);

        var otherBranchTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Branch");
        otherBranchTableColumn.setId(branch);
        otherBranchTableColumn.setPrefWidth(width);
        otherBranchTableColumn.setCellValueFactory(p -> p.getValue().get().branchProperty(branch));
        tableView.getColumns().add(otherBranchTableColumn);

        branchNames.add(branch);
    }

    private void updateSituationSelectors() {

    }

}
