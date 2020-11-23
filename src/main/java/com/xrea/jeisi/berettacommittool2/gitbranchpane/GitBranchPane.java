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
import static com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusPane.setRepositoryDisplayName;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.gitthread.GitBranchCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private final ConfigInfo configInfo;
    private final ErrorLogWindow errorLogWindow;
    private final AtomicInteger refreshThreadCounter = new AtomicInteger();
    private final ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.CHECKED);

    public GitBranchPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.errorLogWindow = new ErrorLogWindow(configInfo);
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
        XmlWriter.writeStartMethod("GitBranchPane.changeTargetRepositories(%s)", target.toString());
        if (target != targetRepository.get()) {
            XmlWriter.writeEndMethodWithReturn();
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
        XmlWriter.writeEndMethod();
    }

    /*
    @Override
    public void setActive(boolean active) {
        this.active = active;
        if(!active) {
            return;
        }
        
        if(requested)
    }
    */
    
    @Override
    public Parent build() {
        targetRepository.addListener((observable, oldValue, newValue) -> {
            changeTargetRepositories(newValue);
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(buildCenter());
        return borderPane;
    }

    private Parent buildCenter() {
        tableView = new TableView();
        tableView.setId("gitBranchTableView");

        var repositoryTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Repository");
        repositoryTableColumn.setPrefWidth(100);
        repositoryTableColumn.setCellValueFactory(p -> p.getValue().get().getRepositoryData().nameProperty());

        var currentBranchTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Current Branch");
        currentBranchTableColumn.setPrefWidth(100);
        currentBranchTableColumn.setCellValueFactory(p -> p.getValue().get().currentBranchProperty());

        tableView.getColumns().addAll(repositoryTableColumn, currentBranchTableColumn);

        if (configInfo != null) {
            List<Double> widths = configInfo.getTableColumnWidth(tableView.getId());
            if (widths != null) {
                for (int index = 0; index < tableView.getColumns().size() && index < widths.size(); ++index) {
                    tableView.getColumns().get(index).setPrefWidth(widths.get(index));
                }
            }
        }

        return tableView;
    }

    @Override
    public Menu buildMenu() {
        Menu menu = new Menu("Branch");
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
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);
    }

    @Override
    public void close() {
        errorLogWindow.close();
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
                    combineBranch();
                }
            }
        });
    }

    private void combineBranch() {
        Set<String> otherBranches = new TreeSet<>();
        Set<String> remoteBranches = new TreeSet<>();
        tableView.getItems().forEach(item -> {
            item.get().getOtherBranches().forEach(branch -> otherBranches.add(branch.get()));
            item.get().getRemoteBranches().forEach(branch -> remoteBranches.add(branch.get()));
        });

        Platform.runLater(() -> {
            otherBranches.forEach(branch -> addBranchColumn(branch));
            remoteBranches.forEach(branch -> addBranchColumn(branch));
        });
    }

    private void addBranchColumn(String branch) {
        var otherBranchTableColumn = new TableColumn<ObjectProperty<GitBranchData>, String>("Branch");
        otherBranchTableColumn.setId(branch);
        otherBranchTableColumn.setPrefWidth(100);
        otherBranchTableColumn.setCellValueFactory(p -> p.getValue().get().branchProperty(branch));
        tableView.getColumns().add(otherBranchTableColumn);

    }

    private void updateSituationSelectors() {

    }

}
