/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.basegitpane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.aggregatedobservablearraylist.AggregatedObservableArrayList;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.InformationLogWindow;
import com.xrea.jeisi.berettacommittool2.filebrowser.FileBrowser;
import com.xrea.jeisi.berettacommittool2.gitcommitwindow.GitCommitWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitAddCommand;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.filterpane.GitStatusDataFilterPane;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCheckIgnoreCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCheckoutCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCherryPickCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactory;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.gitthread.GitDiffCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitMergeCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitMergeToolCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitRebaseCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitRestoreCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitRevertCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitRmCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.gitthread.GitUnstageCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitkCommand;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesUtility;
import com.xrea.jeisi.berettacommittool2.situationselector.DeleteSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddAllSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddPatchSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddPredicate;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddUpdatePredicate;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddUpdateSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCheckIgnoreSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCheckoutHeadSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCheckoutOursTheirsSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCherryPickAbortSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCommitSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitDiffCachedSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitLogSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitMergeAbortSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitMergeToolSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRebaseAbortSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRebaseContinueSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRemoveSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRestoreResetSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRevertAbortSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.HierarchyMenuSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.MultiSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SituationSelector;
import com.xrea.jeisi.berettacommittool2.xmlwriter.LogWriter;
//import com.xrea.jeisi.berettacommittool2.situationselector.SituationVisible;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author jeisi
 */
public class GitStatusPane implements BaseGitPane {

    private boolean active = false;
    private GitCommandFactory gitCommandFactory = new GitCommandFactoryImpl();
    private RepositoriesInfo repositories;
    private TableView<GitStatusData> tableView;
    private GitCommitWindow commitWindow;
    private final ConfigInfo configInfo;
    private final GitStatusDataFilterPane filterPane;
    private final AtomicInteger refreshThreadCounter = new AtomicInteger();
    private final ErrorLogWindow errorLogWindow;
    private final InformationLogWindow informationLogWindow;
    private final ProgressWindow progressWindow;
    private final SituationSelector singleSelectionSituationSelector = new SituationSelector();
    private final SituationSelector multiSelectionSituationSelector = new SituationSelector();
    private final SituationSelector gitAddMenuSituationSelector = new SituationSelector();
    private final SituationSelector gitAddSituationSelector = new SituationSelector();
    private final SituationSelector gitAddPatchSituationSelector = new SituationSelector();
    private final SituationSelector gitAddUpdateSituationSelector = new SituationSelector();
    private final SituationSelector gitAddAllSituationSelector = new SituationSelector();
    private final SituationSelector gitCommitSituationSelector = new SituationSelector();
    private final SituationSelector gitUnstageSituationSelector = new SituationSelector();
    private final SituationSelector gitUnstageSingleSituationSelector = new SituationSelector();
    private final SituationSelector gitCheckoutMenuSituationSelector = new SituationSelector();
    private final SituationSelector gitCheckoutHeadSituationSelector = new SituationSelector();
    private final SituationSelector gitCheckoutOursTheirsSituationSelector = new SituationSelector();
    private final SituationSelector gitCheckIgnoreSituationSelector = new SituationSelector();
    private final SituationSelector gitDiffToolSituationSelector = new SituationSelector();
    private final SituationSelector gitDiffCachedSituationSelector = new SituationSelector();
    private final SituationSelector gitLogSituationSelector = new SituationSelector();
    private final SituationSelector gitMergeToolSituationSelector = new SituationSelector();
    private final SituationSelector gitMergeAbortSituationSelector = new SituationSelector();
    private final SituationSelector gitRmSituationSelector = new SituationSelector();
    private final SituationSelector gitRevertAbortSituationSelector = new SituationSelector();
    private final SituationSelector gitCherryPickAbortSituationSelector = new SituationSelector();
    private final SituationSelector gitRebaseAbortSituationSelector = new SituationSelector();
    private final SituationSelector gitRebaseContinueSituationSelector = new SituationSelector();
    private final SituationSelector gitRebaseMenuSituationSelector = new SituationSelector();
    private final SituationSelector deleteSituationSelector = new SituationSelector();
    private final SituationSelector gitRestoreMenuSituationSelector = new SituationSelector();
    private final SituationSelector gitRestoreResetSituationSelector = new SituationSelector();
    private final ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.SELECTED);
    private boolean requestedUpdateRepositoriesList = false;
    private Menu menu;

    public GitStatusPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.progressWindow = new ProgressWindow(configInfo);
        this.errorLogWindow = new ErrorLogWindow(configInfo);
        this.informationLogWindow = new InformationLogWindow(configInfo);
        this.filterPane = new GitStatusDataFilterPane(configInfo, "gitstatuspane");
    }

    public int getRefreshThreadCounter() {
        return refreshThreadCounter.get();
    }

    @Override
    public String getTitle() {
        return "Status";
    }

    @Override
    public ObjectProperty<TargetRepository> targetRepositoryProperty() {
        return targetRepository;
    }

    public void setGitCommandFactory(GitCommandFactory gitCommandFactory) {
        this.gitCommandFactory = gitCommandFactory;
    }

    @Override
    public void close() {
        errorLogWindow.close();
        informationLogWindow.close();
    }

    @Override
    public void setUp() {
        updateSituationSelectors();
    }

    @Override
    public void setRepositories(RepositoriesInfo work) {
        LogWriter.writeMessage("GitStatusPane.setRepositories()", "work=" + work.toString());

        if (this.repositories != null) {
            throw new RuntimeException("setRepositories() を実行するのは一回だけです。");
        }

        this.repositories = work;

        //reflectRepositories();
    }

    /*private*/ void reflectRepositories() {
        LogWriter.writeMessage("GitStatusPane.reflectRepositories()", "begin");

        RepositoriesInfo work = this.repositories;

        ListChangeListener<RepositoryData> selectedListener = (change) -> changeTargetRepositories(TargetRepository.SELECTED);
        work.getSelected().addListener(selectedListener);
        ListChangeListener<RepositoryData> changedListener = (change) -> changeTargetRepositories(TargetRepository.CHECKED);
        work.getChecked().addListener(changedListener);

        var gitCommitSelectionSituation = new GitCommitSelectionSituation(repositories, targetRepository);
        gitCommitSituationSelector.setSituation(gitCommitSelectionSituation);
        gitRevertAbortSituationSelector.setSituation(new GitRevertAbortSelectionSituation(repositories, targetRepository));
        gitCherryPickAbortSituationSelector.setSituation(new GitCherryPickAbortSelectionSituation(repositories, targetRepository));
        gitRebaseAbortSituationSelector.setSituation(new GitRebaseAbortSelectionSituation(repositories, targetRepository));
        gitRebaseContinueSituationSelector.setSituation(new GitRebaseContinueSelectionSituation(repositories, targetRepository));
        gitMergeAbortSituationSelector.setSituation(new GitMergeAbortSelectionSituation(repositories, targetRepository));

        changeTargetRepositories(targetRepository.get());
    }

    @Override
    public void saveConfig() {
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);

        filterPane.saveConfig();
    }

    private void changeTargetRepositories(TargetRepository target) {
        LogWriter.writeMessage("GitStatusPane.changeTargetRepositories()", "begin");
        LogWriter.writeMessage("GitStatusPane.changeTargetRepositories()", "active=" + Boolean.toString(active));

        // 例えば、現在 SELECT の状態のときに、Checked のリストが変更されても関係ないので、何もしない。
        if (target != targetRepository.get()) {
            return;
        }

        requestedUpdateRepositoriesList = true;
        if (!active) {
            return;
        }
        processChangeTargetRepositories();
    }

    private void processChangeTargetRepositories() {
        if (!requestedUpdateRepositoriesList) {
            return;
        }

        requestedUpdateRepositoriesList = false;

        ObservableList<RepositoryData> targetRepositories = getTargetRepositories();
        AggregatedObservableArrayList aggregated = new AggregatedObservableArrayList();
        targetRepositories.forEach(e -> aggregated.appendList(e.getGitStatusDatas()));
        var filteredList = new FilteredList<GitStatusData>(aggregated.getAggregatedList());
        var sortableData = new SortedList<GitStatusData>(filteredList);
        tableView.setItems(sortableData);
        sortableData.comparatorProperty().bind(tableView.comparatorProperty());
        filterPane.setFilteredList(filteredList);
        updateSituationSelectors();
    }

    /*
    private void changeTargetRepositories(TargetRepository target) {
        if (target != targetRepository.get()) {
            return;
        }

        ObservableList<RepositoryData> targetRepositories = getTargetRepositories();
        AggregatedObservableArrayList aggregated = new AggregatedObservableArrayList();
//        if(targetRepositories.size() > 0)
//            LogWriter.writeObject("GitStatusPane.changeTargetRepositories()", "e.getGitStatusDatas()", targetRepositories.get(0).getGitStatusDatas());
        targetRepositories.forEach(e -> aggregated.appendList(e.getGitStatusDatas()));
        var filteredList = new FilteredList<GitStatusData>(aggregated.getAggregatedList());
        var sortableData = new SortedList<GitStatusData>(filteredList);
        tableView.setItems(sortableData);
        sortableData.comparatorProperty().bind(tableView.comparatorProperty());
        filterPane.setFilteredList(filteredList);
        updateSituationSelectors();
    }
     */
    private ObservableList<RepositoryData> getTargetRepositories() {
        return repositories.getTarget(targetRepository.get());
    }

    @Override
    public void setActive(boolean active) {
        LogWriter.writeMessage("GitStatusPane.setActive(active)", "active=" + Boolean.toString(active));

        menu.setVisible(active);

        this.active = active;
        if (!active) {
            return;
        }

        reflectRepositories();

        long numNullRepository = repositories.getDatas().stream().filter(p -> p.getGitStatusDatas() == null).count();
        if (repositories.getDatas().size() == 0 || numNullRepository > 0) {
            refreshAll();
        }
    }

    @Override
    public void clearAll() {
        for (RepositoryData repository : repositories.getDatas()) {
            GitThread thread = GitThreadMan.get(repository.getPath().toString());
            thread.addCommand(() -> {
                clearRepository(repository);
            });
        }
    }

    @Override
    public void refreshAll() {
        reflectRepositories();
        refreshCommon(repositories.getDatas());
    }

    @Override
    public void refreshChecked() {
        refreshCommon(repositories.getChecked());
    }

    @Override
    public void refreshSelected() {
        refreshCommon(repositories.getSelected());
    }

    private void refreshTarget() {
        switch (targetRepository.get()) {
            case CHECKED:
                refreshChecked();
                break;
            case SELECTED:
                refreshSelected();
                break;
            default:
                assert (false);
                break;
        }
    }

    private void refreshIgnoredSelected() {
        ObservableList<RepositoryData> datas = repositories.getSelected();
        datas.forEach((var repositoryData) -> {
            refreshRepository(repositoryData, (command, repository) -> {
                command.setIgnored(true);
                return command.status(repository);
            });
        });
    }

    private void refreshCommon(ObservableList<RepositoryData> datas) {
        datas.forEach((var repositoryData) -> {
            refreshRepository(repositoryData, (command, repository) -> command.status(repository));
        });
    }

    private void refreshRepository(RepositoryData repository, GitStatusExecutor gitStatusExecutor) {
        refreshThreadCounter.incrementAndGet();
        repository.displayNameProperty().set(String.format("%s [updating...]", repository.nameProperty().get()));

        GitThread thread = GitThreadMan.get(repository.getPath().toString());
        thread.addCommand(() -> {
            initRepository(repository);
            refreshRepositoryCore(repository, gitStatusExecutor);
        });
    }

    private void initRepository(RepositoryData repository) {
        if (repository.getGitStatusDatas() == null) {
            repository.setGitStatusDatas(FXCollections.observableArrayList());
        } else {
            repository.getGitStatusDatas().clear();
        }
    }

    private void clearRepository(RepositoryData repository) {
        repository.resetGitStatusDatas();
    }

    private void refreshRepositoryCore(RepositoryData repository, GitStatusExecutor gitStatusExecutor) {
        GitStatusCommand command = gitCommandFactory.createStatusCommand(repository.getPath(), configInfo);
        List<GitStatusData> gitStatusDatas;
        try {
            gitStatusDatas = gitStatusExecutor.exec(command, repository);
        } catch (GitConfigException | IOException | InterruptedException e) {
            RepositoriesUtility.setErrorName(repository, e, errorLogWindow);
            return;
        }
        /*
        try {
            gitStatusDatas = gitStatusExecutor.exec(command, repository);
        } catch (RepositoryNotFoundException ex) {
            Platform.runLater(() -> showError(ex));
            repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getShortMessage()));
            return;
        } catch (IOException | GitConfigException | InterruptedException ex) {
            Platform.runLater(() -> showError(ex));
            repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getMessage()));
            return;
        }
         */
        Platform.runLater(() -> {
            repository.getGitStatusDatas().setAll(gitStatusDatas);
            setRepositoryDisplayName(repository);
            updateSituationSelectors();
            refreshThreadCounter.decrementAndGet();
        });
    }

    public static void setRepositoryDisplayName(RepositoryData repository) {
        ObservableList<GitStatusData> gitStatusDatas = repository.getGitStatusDatas();
        String name;
        if (repository.isReverting()) {
            name = String.format("[Revert中] %s", repository.nameProperty().get());
        } else if (repository.isCherryPicking()) {
            name = String.format("[cherry-pick中] %s", repository.nameProperty().get());
        } else if (repository.isRebasing()) {
            name = String.format("[リベース中] %s", repository.nameProperty().get());
        } else if (repository.isMerging()) {
            name = String.format("[マージ中] %s", repository.nameProperty().get());
        } else {
            name = repository.nameProperty().get();
        }
        if (gitStatusDatas.isEmpty()) {
            repository.displayNameProperty().set(String.format("%s", name));
        } else {
            repository.displayNameProperty().set(String.format("%s (%d)", name, gitStatusDatas.size()));
        }
    }

    @Override
    public Parent build() {
        tableView = new TableView<>();
        tableView.setId("gitStatusTableView");
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        var indexTableColumn = new TableColumn<GitStatusData, String>("X");
        indexTableColumn.setPrefWidth(30);
        indexTableColumn.setSortable(true);
        indexTableColumn.setCellValueFactory(p -> p.getValue().indexStatusProperty());
        indexTableColumn.setStyle("-fx-alignment: center;");

        var workTreeTableColumn = new TableColumn<GitStatusData, String>("Y");
        workTreeTableColumn.setPrefWidth(30);
        workTreeTableColumn.setCellValueFactory(p -> p.getValue().workTreeStatusProperty());
        workTreeTableColumn.setStyle("-fx-alignment: center;");

        var fileTableColumn = new TableColumn<GitStatusData, String>("File");
        fileTableColumn.setPrefWidth(300);
        fileTableColumn.setCellValueFactory(p -> p.getValue().fileNameProperty());

        var repositoryTableColumn = new TableColumn<GitStatusData, String>("Repository");
        repositoryTableColumn.setPrefWidth(300);
        repositoryTableColumn.setCellValueFactory(p -> p.getValue().getRepositoryData().nameProperty());

        tableView.getColumns().addAll(indexTableColumn, workTreeTableColumn, fileTableColumn, repositoryTableColumn);

        if (configInfo != null) {
            List<Double> widths = configInfo.getTableColumnWidth(tableView.getId());
            if (widths != null) {
                for (int index = 0; index < tableView.getColumns().size() && index < widths.size(); ++index) {
                    tableView.getColumns().get(index).setPrefWidth(widths.get(index));
                }
            }
        }

        var selectionModel = tableView.getSelectionModel();
        singleSelectionSituationSelector.setSituation(new SingleSelectionSituation<>(tableView.getSelectionModel()));
        multiSelectionSituationSelector.setSituation(new MultiSelectionSituation<>(tableView.getSelectionModel()));
        var gitAddSelectionSituation = new GitAddSelectionSituation(tableView.getSelectionModel());
        gitAddSituationSelector.setSituation(gitAddSelectionSituation);
        //gitAddSituationVisible.setSituation(gitAddSelectionSituation);
        var gitAddSingleSelectionSituation = new GitAddPatchSelectionSituation(selectionModel);
        gitAddPatchSituationSelector.setSituation(gitAddSingleSelectionSituation);
        //gitAddSingleSituationVisible.setSituation(gitAddSingleSelectionSituation);
        gitAddUpdateSituationSelector.setSituation(new GitAddUpdateSelectionSituation(tableView));
        gitAddAllSituationSelector.setSituation(new GitAddAllSelectionSituation(tableView));
        var gitUnstageSelectionSituation = new GitUnstageSelectionSituation(selectionModel);
        gitUnstageSituationSelector.setSituation(gitUnstageSelectionSituation);
        //gitUnstageSituationVisible.setSituation(gitUnstageSelectionSituation);
        var gitUnstageSingleSelectionSituation = new GitUnstageSingleSelectionSituation(selectionModel);
        gitUnstageSingleSituationSelector.setSituation(gitUnstageSingleSelectionSituation);
        //gitUnstageSingleSituationVisible.setSituation(gitUnstageSingleSelectionSituation);
        var gitCheckoutHeadSelectionSituation = new GitCheckoutHeadSelectionSituation(selectionModel);
        gitCheckoutHeadSituationSelector.setSituation(gitCheckoutHeadSelectionSituation);
        //gitCheckoutHeadSituationVisible.setSituation(gitCheckoutHeadSelectionSituation);
        gitCheckoutOursTheirsSituationSelector.setSituation(new GitCheckoutOursTheirsSelectionSituation(selectionModel));
        //gitCommitSituationVisible.setSituation(gitCommitSelectionSituation);
        gitRmSituationSelector.setSituation(new GitRemoveSelectionSituation(selectionModel));
        deleteSituationSelector.setSituation(new DeleteSelectionSituation(selectionModel));
        gitCheckIgnoreSituationSelector.setSituation(new GitCheckIgnoreSelectionSituation(selectionModel));
        gitLogSituationSelector.setSituation(new GitLogSelectionSituation(selectionModel));
        gitMergeToolSituationSelector.setSituation(new GitMergeToolSelectionSituation(selectionModel));
        gitDiffCachedSituationSelector.setSituation(new GitDiffCachedSelectionSituation(selectionModel));
        gitRestoreResetSituationSelector.setSituation(new GitRestoreResetSelectionSituation(selectionModel));

        tableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Integer> change) {
                updateSituationSelectors();
            }
        });

        tableView.setContextMenu(buildContextMenu());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setBottom(filterPane.build());

        targetRepository.addListener((observal, oldValue, newValue) -> {
            requestedUpdateRepositoriesList = true;
            if (!active) {
                return;
            }
            processChangeTargetRepositories();
        });

        //return tableView;
        return borderPane;
    }

    @Override
    public Menu buildMenu() {
        MenuItem addMenuItem = new MenuItem("git add <file>...");
        addMenuItem.setId("gitStatusAddMenuItem");
        addMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN));
        addMenuItem.setOnAction(e -> gitAdd());
        gitAddSituationSelector.getEnableMenuItems().add(addMenuItem);

        MenuItem add_pMenuItem = new MenuItem("git add -p <file>");
        add_pMenuItem.setId("gitStatusAddpMenuItem");
        gitAddPatchSituationSelector.getEnableMenuItems().add(add_pMenuItem);
        // TODO: 選択できる条件はほぼ git add と同じだが、厳密にはまだブランチがない時は選択不可。

        MenuItem add_uMenuItem = new MenuItem("git add -u");
        add_uMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN));
        add_uMenuItem.setId("gitStatusAddUpdateMenuItem");
        add_uMenuItem.setOnAction(eh -> gitAddUpdate());
        gitAddUpdateSituationSelector.getEnableMenuItems().add(add_uMenuItem);

        MenuItem addAllMenuItem = new MenuItem("git add -A");
        addAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN));
        addAllMenuItem.setOnAction(eh -> gitAddAll());
        gitAddAllSituationSelector.getEnableMenuItems().add(addAllMenuItem);

        Menu addSubMenu = new Menu("git add");
        addSubMenu.setId("gitStatusAddSubMenu");
        addSubMenu.getItems().addAll(addMenuItem, add_pMenuItem, add_uMenuItem, addAllMenuItem);
        gitAddMenuSituationSelector.setSituation(new HierarchyMenuSelectionSituation(addMenuItem, add_pMenuItem, add_uMenuItem, addAllMenuItem));
        gitAddMenuSituationSelector.getEnableMenuItems().add(addSubMenu);

        MenuItem gitRestoreMenuItem = new MenuItem("git restore <file>...");
        gitRestoreMenuItem.setOnAction(eh -> gitCheckoutHyphen());
        gitCheckoutHeadSituationSelector.getEnableMenuItems().add(gitRestoreMenuItem);

        MenuItem gitRestoreStagedMenuItem = new MenuItem("git restore --staged <file>...");
        gitRestoreStagedMenuItem.setOnAction(eh -> gitUnstage());
        gitUnstageSituationSelector.getEnableMenuItems().add(gitRestoreStagedMenuItem);

        MenuItem gitRestoreStagedWorktreeMenuItem = new MenuItem("git restore --source=HEAD --staged --worktree <file>...");
        gitRestoreStagedWorktreeMenuItem.setOnAction(eh -> gitRestoreReset());
        gitRestoreResetSituationSelector.getEnableMenuItems().add(gitRestoreStagedWorktreeMenuItem);

        Menu gitRestoreMenu = new Menu("git restore");
        gitRestoreMenu.getItems().addAll(gitRestoreMenuItem, gitRestoreStagedMenuItem, gitRestoreStagedWorktreeMenuItem);
        gitRestoreMenuSituationSelector.setSituation(new HierarchyMenuSelectionSituation(gitRestoreMenuItem, gitRestoreStagedMenuItem, gitRestoreStagedWorktreeMenuItem));
        gitRestoreMenuSituationSelector.getEnableMenuItems().add(gitRestoreMenu);

        MenuItem checkoutHyphenMenuItem = new MenuItem("git checkout -- <file>...");
        checkoutHyphenMenuItem.setOnAction(eh -> gitCheckoutHyphen());
        gitCheckoutHeadSituationSelector.getEnableMenuItems().add(checkoutHyphenMenuItem);

        MenuItem checkoutOursMenuItem = new MenuItem("git checkout --ours <file>...");
        checkoutOursMenuItem.setOnAction(eh -> gitCheckoutOurs());
        gitCheckoutOursTheirsSituationSelector.getEnableMenuItems().add(checkoutOursMenuItem);

        MenuItem checkoutTheirsMenuItem = new MenuItem("git checkout --theirs <file>...");
        checkoutTheirsMenuItem.setOnAction(eh -> gitCheckoutTheirs());
        gitCheckoutOursTheirsSituationSelector.getEnableMenuItems().add(checkoutTheirsMenuItem);

        Menu checkoutSubMenu = new Menu("git checkout");
        checkoutSubMenu.getItems().addAll(checkoutHyphenMenuItem, checkoutOursMenuItem, checkoutTheirsMenuItem);
        gitCheckoutMenuSituationSelector.setSituation(new HierarchyMenuSelectionSituation(checkoutHyphenMenuItem, checkoutOursMenuItem, checkoutTheirsMenuItem));
        gitCheckoutMenuSituationSelector.getEnableMenuItems().add(checkoutSubMenu);

        MenuItem unstageMenuItem = new MenuItem("git reset HEAD <file>...");
        unstageMenuItem.setId("gitStatusUnstageMenuItem");
        unstageMenuItem.setOnAction(e -> gitUnstage());
        gitUnstageSituationSelector.getEnableMenuItems().add(unstageMenuItem);

        MenuItem rmMenuItem = new MenuItem("git rm -f <file>...");
        rmMenuItem.setOnAction(e -> gitRm());
        gitRmSituationSelector.getEnableMenuItems().add(rmMenuItem);

        MenuItem diffMenuItem = new MenuItem("git difftool <file>");
        diffMenuItem.setId("gitStatusDiffMenuItem");
        diffMenuItem.setOnAction(eh -> gitDiff());
        diffMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.ALT_DOWN));
        gitAddPatchSituationSelector.getEnableMenuItems().add(diffMenuItem);

        MenuItem diffCachedMenuItem = new MenuItem("git difftool --cached <file>");
        diffCachedMenuItem.setId("gitStatusDiffCachedMenuItem");
        diffCachedMenuItem.setOnAction(eh -> gitDiffCached());
        diffCachedMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN));
        gitDiffCachedSituationSelector.getEnableMenuItems().add(diffCachedMenuItem);

        Menu diffSubMenu = new Menu("git difftool");
        diffSubMenu.getItems().addAll(diffMenuItem, diffCachedMenuItem);
        gitDiffToolSituationSelector.setSituation(new HierarchyMenuSelectionSituation(diffMenuItem, diffCachedMenuItem));
        gitDiffToolSituationSelector.getEnableMenuItems().add(diffSubMenu);

        MenuItem mergeToolMenuItem = new MenuItem("git mergetool <file>");
        mergeToolMenuItem.setOnAction(eh -> gitMergeTool());
        gitMergeToolSituationSelector.getEnableMenuItems().add(mergeToolMenuItem);

        MenuItem mergeAbortMenuItem = new MenuItem("git merge --abort");
        mergeAbortMenuItem.setId("GitStatusPaneMergeAbortMenuItem");
        mergeAbortMenuItem.setOnAction(eh -> gitMergeAbort());
        gitMergeAbortSituationSelector.getEnableMenuItems().add(mergeAbortMenuItem);

        MenuItem revertContinueMenuItem = new MenuItem("git revert --continue");
        revertContinueMenuItem.setId("GitStatusPaneRevertContinueMenuItem");
        revertContinueMenuItem.setOnAction(eh -> gitRevertContinue());
        gitRevertAbortSituationSelector.getEnableMenuItems().add(revertContinueMenuItem);

        MenuItem revertAbortMenuItem = new MenuItem("git revert --abort");
        revertAbortMenuItem.setId("GitStatusPaneRevertAbortMenuItem");
        revertAbortMenuItem.setOnAction(eh -> gitRevertAbort());
        gitRevertAbortSituationSelector.getEnableMenuItems().add(revertAbortMenuItem);

        MenuItem revertSkipMenuItem = new MenuItem("git revert --skip");
        revertSkipMenuItem.setId("GitStatusPaneRevertSkipMenuItem");
        revertSkipMenuItem.setOnAction(eh -> gitRevertSkip());
        gitRevertAbortSituationSelector.getEnableMenuItems().add(revertSkipMenuItem);

        Menu revertMenu = new Menu("git revert");
        revertMenu.getItems().addAll(revertContinueMenuItem, revertAbortMenuItem, revertSkipMenuItem);
        gitRevertAbortSituationSelector.getEnableMenuItems().add(revertMenu);

        MenuItem cherryPickContinueMenuItem = new MenuItem("git cherry-pick --continue");
        cherryPickContinueMenuItem.setId("GitStatusPaneCherryPickContinueMenuItem");
        cherryPickContinueMenuItem.setOnAction(eh -> gitCommit());
        gitCherryPickAbortSituationSelector.getEnableMenuItems().add(cherryPickContinueMenuItem);

        MenuItem cherryPickAbortMenuItem = new MenuItem("git cherry-pick --abort");
        cherryPickAbortMenuItem.setId("GitStatusPaneCherryPickAbortMenuItem");
        cherryPickAbortMenuItem.setOnAction(eh -> gitCherryPickAbort());
        gitCherryPickAbortSituationSelector.getEnableMenuItems().add(cherryPickAbortMenuItem);

        MenuItem cherryPickSkipMenuItem = new MenuItem("git cherry-pick --skip");
        cherryPickSkipMenuItem.setId("GitStatusPaneCherryPickSkipMenuItem");
        cherryPickSkipMenuItem.setOnAction(eh -> gitCherryPickSkip());
        gitCherryPickAbortSituationSelector.getEnableMenuItems().add(cherryPickSkipMenuItem);

        Menu cherryPickMenu = new Menu("git cherry-pick");
        cherryPickMenu.getItems().addAll(cherryPickContinueMenuItem, cherryPickAbortMenuItem, cherryPickSkipMenuItem);
        gitCherryPickAbortSituationSelector.getEnableMenuItems().add(cherryPickMenu);

        MenuItem rebaseContinueMenuItem = new MenuItem("git rebase --continue");
        rebaseContinueMenuItem.setId("GitStatusPaneRebaseContinueMenuItem");
        rebaseContinueMenuItem.setOnAction(eh -> gitRebaseContinue());
        gitRebaseContinueSituationSelector.getEnableMenuItems().add(rebaseContinueMenuItem);

        MenuItem rebaseAbortMenuItem = new MenuItem("git rebase --abort");
        rebaseAbortMenuItem.setId("GitStatusPaneRebaseAbortMenuItem");
        rebaseAbortMenuItem.setOnAction(eh -> gitRebaseAbort());
        gitRebaseAbortSituationSelector.getEnableMenuItems().add(rebaseAbortMenuItem);

        MenuItem rebaseSkipMenuItem = new MenuItem("git rebase --skip");
        rebaseSkipMenuItem.setOnAction(eh -> gitRebaseSkip());
        gitRebaseAbortSituationSelector.getEnableMenuItems().add(rebaseSkipMenuItem);

        Menu rebaseMenu = new Menu("git rebase");
        rebaseMenu.getItems().addAll(rebaseContinueMenuItem, rebaseAbortMenuItem, rebaseSkipMenuItem);
        gitRebaseMenuSituationSelector.setSituation(new HierarchyMenuSelectionSituation(rebaseContinueMenuItem, rebaseAbortMenuItem, rebaseSkipMenuItem));
        gitRebaseMenuSituationSelector.getEnableMenuItems().add(rebaseMenu);

        MenuItem gitkMenuItem = new MenuItem("gitk <file>");
        gitkMenuItem.setOnAction(eh -> gitk(/*isAll=*/false, /*isSimplifyMerges=*/ false));

        MenuItem gitkAllMenuItem = new MenuItem("gitk --all <file>");
        gitkAllMenuItem.setOnAction(eh -> gitk(/*isAll=*/true, /*isSimplifyMerges=*/ false));

        MenuItem gitkSimplifyMergesMenuItem = new MenuItem("gitk --simplify-merges <file>");
        gitkSimplifyMergesMenuItem.setOnAction(eh -> gitk(/*isAll=*/false, /*isSimplifyMerges=*/ true));

        MenuItem gitkAllSimpifyMergesMenuItem = new MenuItem("gitk --all --simplify-merges <file>");
        gitkAllSimpifyMergesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.ALT_DOWN));
        gitkAllSimpifyMergesMenuItem.setOnAction(eh -> gitk(/*isAll=*/true, /*isSimplifyMerges=*/ true));

        MenuItem gitkWithOptionsMenuItem = new MenuItem("gitk...");

        Menu gitkSubMenu = new Menu("gitk");
        gitkSubMenu.getItems().addAll(gitkMenuItem, gitkAllMenuItem, gitkSimplifyMergesMenuItem, gitkAllSimpifyMergesMenuItem, gitkWithOptionsMenuItem);
        gitLogSituationSelector.getEnableMenuItems().addAll(gitkSubMenu.getItems());
        gitLogSituationSelector.getEnableMenuItems().add(gitkSubMenu);

        MenuItem commitMenuItem = new MenuItem("git commit");
        commitMenuItem.setId("gitStatusCommitMenuItem");
        commitMenuItem.setDisable(true);
        commitMenuItem.setOnAction(e -> gitCommit());
        commitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN));
        gitCommitSituationSelector.getEnableMenuItems().add(commitMenuItem);

        MenuItem statusIgnoredMenuItem = new MenuItem("git status --ignored");
        statusIgnoredMenuItem.setOnAction(eh -> refreshIgnoredSelected());
        gitCommitSituationSelector.getEnableMenuItems().add(statusIgnoredMenuItem);

        MenuItem checkIgnoreMenuItem = new MenuItem("git check-ignore <file>");
        checkIgnoreMenuItem.setOnAction(eh -> gitCheckIgnore());
        gitCheckIgnoreSituationSelector.getEnableMenuItems().add(checkIgnoreMenuItem);

        MenuItem deleteMenuItem = new MenuItem("rm <file>...");
        deleteMenuItem.setOnAction(eh -> deleteFile());
        deleteSituationSelector.getEnableMenuItems().add(deleteMenuItem);

        CheckMenuItem filterMenuItem = new CheckMenuItem("Filter");
        filterMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        filterMenuItem.setOnAction(eh -> {
            boolean isSelected = ((CheckMenuItem) eh.getSource()).isSelected();
            filterPane.setEnabled(isSelected);
        });
        filterMenuItem.setSelected(filterPane.isEnabled());

        MenuItem copyFilePathMenuItem = new MenuItem("ファイルのフルパスをコピー");
        copyFilePathMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        copyFilePathMenuItem.setOnAction(eh -> copyFilePathToClipBoard());
        singleSelectionSituationSelector.getEnableMenuItems().add(copyFilePathMenuItem);

        MenuItem openFileManagerMenuItem = createOpenFileManagerMenuItem();

        menu = new Menu("Status");
        menu.setId("gitStatusMenu");
        menu.getItems().addAll(addSubMenu, gitRestoreMenu, checkoutSubMenu,
                unstageMenuItem, rmMenuItem, diffSubMenu, mergeToolMenuItem, mergeAbortMenuItem, revertMenu, cherryPickMenu, rebaseMenu,
                gitkSubMenu, commitMenuItem, statusIgnoredMenuItem, checkIgnoreMenuItem, deleteMenuItem, new SeparatorMenuItem(),
                filterMenuItem, copyFilePathMenuItem, openFileManagerMenuItem);
        return menu;
    }

    public Parent buildToolBar() {
        Button commitButton = new Button("commit");
        commitButton.setTooltip(new Tooltip("git commit"));
        commitButton.setOnAction(eh -> gitCommit());
        gitCommitSituationSelector.getVisibleButotns().add(commitButton);

        Button addButton = new Button("+");
        addButton.setTooltip(new Tooltip("git add <file>..."));
        addButton.setOnAction(eh -> gitAdd());
        gitAddSituationSelector.getVisibleButotns().add(addButton);

        Button unstageButton = new Button("-");
        unstageButton.setTooltip(new Tooltip("git restore --staged <file>..."));
        unstageButton.setOnAction(eh -> gitUnstage());
        gitUnstageSituationSelector.getVisibleButotns().add(unstageButton);

        Button mergeToolButton = new Button("mergetool");
        mergeToolButton.setTooltip(new Tooltip("git mergetool <file>"));
        mergeToolButton.setOnAction(eh -> gitMergeTool());
        gitMergeToolSituationSelector.getVisibleButotns().add(mergeToolButton);

        Button checkoutHeadButton = new Button("restore");
        checkoutHeadButton.setTooltip(new Tooltip("git restore <file>... (ローカルの編集の破棄)"));
        checkoutHeadButton.setOnAction(eh -> gitCheckoutHyphen());
        gitCheckoutHeadSituationSelector.getVisibleButotns().add(checkoutHeadButton);

        Button checkoutTheirsButton = new Button("checkout --theirs");
        checkoutTheirsButton.setTooltip(new Tooltip("git checkout --theirs <file>..."));
        checkoutTheirsButton.setOnAction(eh -> gitCheckoutTheirs());
        gitCheckoutOursTheirsSituationSelector.getVisibleButotns().add(checkoutTheirsButton);

        Button checkoutOursButton = new Button("checkout --ours");
        checkoutOursButton.setTooltip(new Tooltip("git checkout --ours <file>..."));
        checkoutOursButton.setOnAction(eh -> gitCheckoutOurs());
        gitCheckoutOursTheirsSituationSelector.getVisibleButotns().add(checkoutOursButton);

        Button diffButton = new Button("diff");
        diffButton.setTooltip(new Tooltip("git difftool <file>"));
        diffButton.setOnAction(eh -> gitDiff());
        gitAddPatchSituationSelector.getVisibleButotns().add(diffButton);

        Button diffCachedButton = new Button("diff");
        diffCachedButton.setTooltip(new Tooltip("git difftool --cached <file>"));
        diffCachedButton.setOnAction(eh -> gitDiffCached());
        gitDiffCachedSituationSelector.getVisibleButotns().add(diffCachedButton);

        Button gitLogButton = new Button("log");
        gitLogButton.setTooltip(new Tooltip("gitk --all --simplify-merges <files>"));
        gitLogButton.setOnAction(eh -> gitk(/*isAll=*/true, /*isSimplifyMerges=*/ true));
        gitLogSituationSelector.getVisibleButotns().add(gitLogButton);

        Button deleteButton = new Button("delete");
        deleteButton.setTooltip(new Tooltip("rm <file>..."));
        deleteButton.setOnAction(eh -> deleteFile());
        deleteSituationSelector.getVisibleButotns().add(deleteButton);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(commitButton, addButton, unstageButton, diffButton, diffCachedButton, mergeToolButton, gitLogButton, checkoutHeadButton, checkoutTheirsButton, checkoutOursButton, deleteButton);
        //hbox.getChildren().addAll(addButton, commitButton);
        hbox.setSpacing(5);
        return hbox;
    }

    private ContextMenu buildContextMenu() {
        MenuItem gitkAllSimpifyMergesMenuItem = new MenuItem("gitk --all --simplify-merges <file>");
        gitkAllSimpifyMergesMenuItem.setOnAction(eh -> gitk(/*isAll=*/true, /*isSimplifyMerges=*/ true));
        gitLogSituationSelector.getVisibleMenuItems().add(gitkAllSimpifyMergesMenuItem);

        MenuItem gitAddMenuItem = new MenuItem("git add <file>...");
        gitAddMenuItem.setOnAction(eh -> gitAdd());
        gitAddSituationSelector.getVisibleMenuItems().add(gitAddMenuItem);

        MenuItem rmMenuItem = new MenuItem("git rm -f <file>...");
        rmMenuItem.setOnAction(e -> gitRm());
        gitRmSituationSelector.getVisibleMenuItems().add(rmMenuItem);

        MenuItem mergeToolMenuItem = new MenuItem("git mergetool <file>");
        mergeToolMenuItem.setOnAction(eh -> gitMergeTool());
        gitMergeToolSituationSelector.getVisibleMenuItems().add(mergeToolMenuItem);

        MenuItem checkoutOursMenuItem = new MenuItem("git checkout --ours <file>...");
        checkoutOursMenuItem.setOnAction(eh -> gitCheckoutOurs());
        gitCheckoutOursTheirsSituationSelector.getVisibleMenuItems().add(checkoutOursMenuItem);

        MenuItem checkoutTheirsMenuItem = new MenuItem("git checkout --theirs <file>...");
        checkoutTheirsMenuItem.setOnAction(eh -> gitCheckoutTheirs());
        gitCheckoutOursTheirsSituationSelector.getVisibleMenuItems().add(checkoutTheirsMenuItem);

        MenuItem checkIgnoreMenuItem = new MenuItem("git check-ignore <file>");
        checkIgnoreMenuItem.setOnAction(eh -> gitCheckIgnore());
        gitCheckIgnoreSituationSelector.getVisibleMenuItems().add(checkIgnoreMenuItem);

        MenuItem deleteMenuItem = new MenuItem("rm <file>...");
        deleteMenuItem.setOnAction(eh -> deleteFile());
        deleteSituationSelector.getVisibleMenuItems().add(deleteMenuItem);

        MenuItem copyFilePathMenuItem = new MenuItem("ファイルのフルパスをコピー");
        copyFilePathMenuItem.setOnAction(eh -> copyFilePathToClipBoard());
        singleSelectionSituationSelector.getEnableMenuItems().add(copyFilePathMenuItem);

        MenuItem openFileManagerMenuItem = createOpenFileManagerMenuItem();

        ContextMenu contextMenu = new ContextMenu(gitkAllSimpifyMergesMenuItem, gitAddMenuItem, rmMenuItem, mergeToolMenuItem, checkoutOursMenuItem, checkoutTheirsMenuItem, checkIgnoreMenuItem,
                deleteMenuItem, copyFilePathMenuItem, openFileManagerMenuItem);
        return contextMenu;
    }

    private MenuItem createOpenFileManagerMenuItem() {
        MenuItem openFileManagerMenuItem = new MenuItem("ファイルマネージャを開く");
        openFileManagerMenuItem.setOnAction(eh -> openFileManager());
        boolean isSupported = FileBrowser.getInstance().isSupportedBrowseFileDir();
        if (!isSupported) {
            openFileManagerMenuItem.setDisable(true);
        } else {
            singleSelectionSituationSelector.getEnableMenuItems().add(openFileManagerMenuItem);
        }
        return openFileManagerMenuItem;
    }

    private void gitCommit() {
        if (commitWindow == null) {
            commitWindow = new GitCommitWindow(configInfo);
            commitWindow.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == true && newValue == false) {
                    commitWindow = null;
                }
            });
            commitWindow.getGitCommitPane().setGitCommandFactory(gitCommandFactory);
            commitWindow.open();
            commitWindow.getGitCommitPane().setRepositoryDatas(getTargetRepositories());
            commitWindow.getGitCommitPane().addEventHandler((e) -> refreshTarget());
        } else {
            commitWindow.toFront();
        }
    }

    private void gitRevertContinue() {
        gitCommit();
    }

    private void gitRevertAbort() {
        gitRevertCommon("--abort");
    }

    private void gitRevertSkip() {
        gitRevertCommon("--skip");
    }

    private void gitRevertCommon(String option) {
        gitCommandForRepositoryCommon(workDir -> {
            GitRevertCommand revertCommand = new GitRevertCommand(workDir, configInfo);
            revertCommand.revert(option);
        });
    }

    private void gitCherryPickAbort() {
        gitCherryPickCommon("--abort");
    }

    private void gitCherryPickSkip() {
        gitCherryPickCommon("--skip");
    }

    private void gitCherryPickCommon(String option) {
        gitCommandForRepositoryCommon(workDir -> {
            GitCherryPickCommand cherryPickCommand = new GitCherryPickCommand(workDir, configInfo);
            cherryPickCommand.cherryPick(option);
        });
    }

    private void gitRebaseContinue() {
        gitRebaseCommon("--continue");
    }

    private void gitRebaseAbort() {
        gitRebaseCommon("--abort");
    }

    private void gitRebaseSkip() {
        gitRebaseCommon("--skip");
    }

    private void gitRebaseCommon(String option) {
        gitCommandForRepositoryCommon(workDir -> {
            GitRebaseCommand rebaseCommand = new GitRebaseCommand(workDir, configInfo);
            rebaseCommand.rebase(option);
        });
    }

    private void gitMergeAbort() {
        gitMergeCommon("--abort");
    }

    private void gitMergeCommon(String option) {
        gitCommandForRepositoryCommon(workDir -> {
            GitMergeCommand rebaseCommand = new GitMergeCommand(workDir, configInfo);
            rebaseCommand.merge(option);
        });
    }

    private void gitCommandForRepositoryCommon(CommandForRepositoryExecutor executor) {
        getTargetRepositories().forEach(repositoryData -> {
            GitThread thread = GitThreadMan.get(repositoryData.getPath().toString());
            thread.addCommand(() -> {
                Path workDir = repositoryData.getPath();
                List<GitStatusData> statusDatas;
                try {
                    executor.exec(workDir);
                    refreshRepositoryCore(repositoryData, (command, repository) -> command.status(repository));
                } catch (IOException | GitConfigException | InterruptedException ex) {
                    showError(ex);
                    return;
                }
            });
        });
    }

    private void gitAdd() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitAddCommand addCommand = gitCommandFactory.createAddCommand(workDir, configInfo);
            addCommand.setProgressWindow(progressWindow);
            addCommand.add(datas);
        });
    }

    // git add -u 相当
    private void gitAddUpdate() {
        Set<RepositoryData> filesPerRepo = getSpecifiedRepositories(new GitAddUpdatePredicate());
        execCommandForRepository(filesPerRepo, (workDir, files) -> {
            GitAddCommand addCommand = gitCommandFactory.createAddCommand(workDir, configInfo);
            addCommand.setProgressWindow(progressWindow);
            addCommand.addUpdate();
        });
    }

    // git add -A
    private void gitAddAll() {
        Set<RepositoryData> filesPerRepo = getSpecifiedRepositories(new GitAddPredicate());
        execCommandForRepository(filesPerRepo, (workDir, files) -> {
            GitAddCommand addCommand = gitCommandFactory.createAddCommand(workDir, configInfo);
            addCommand.setProgressWindow(progressWindow);
            addCommand.addAll();
        });
    }

    private void gitUnstage() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, files) -> {
            GitUnstageCommand unstageCommand = gitCommandFactory.createUnstageCommand(workDir, configInfo);
            unstageCommand.setProgressWindow(progressWindow);
            unstageCommand.unstage(files);
        });
    }

    // git restore --source=HEAD --staged --worktree <file>...
    private void gitRestoreReset() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitRestoreCommand restoreCommand = new GitRestoreCommand(workDir, configInfo);
            restoreCommand.setProgressWindow(progressWindow);
            restoreCommand.restoreReset(datas);
        });
    }

    private void gitRm() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitRmCommand rmCommand = new GitRmCommand(workDir, configInfo);
            rmCommand.setProgressWindow(progressWindow);
            rmCommand.rm(datas);
        });
    }

    // git checkout -- <file>...
    private void gitCheckoutHyphen() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "ローカルの作業内容を破棄します。\nよろしいですか？", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == ButtonType.NO) {
            return;
        }

        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitCheckoutCommand checkoutCommand = gitCommandFactory.createCheckoutCommand(workDir, configInfo);
            checkoutCommand.setProgressWindow(progressWindow);
            checkoutCommand.checkoutHead(datas);
        });
    }

    // git checkout --ours
    private void gitCheckoutOurs() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitCheckoutCommand checkoutCommand = gitCommandFactory.createCheckoutCommand(workDir, configInfo);
            checkoutCommand.setProgressWindow(progressWindow);
            checkoutCommand.checkoutOurs(datas);
        });
    }

    // git checkout --theirs
    private void gitCheckoutTheirs() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            GitCheckoutCommand checkoutCommand = gitCommandFactory.createCheckoutCommand(workDir, configInfo);
            checkoutCommand.setProgressWindow(progressWindow);
            checkoutCommand.checkoutTheirs(datas);
        });
    }

    private void gitDiff() {
        var selectedItem = getSelectedFile();
        GitDiffCommand diffCommand = gitCommandFactory.createGitDiffCommand(selectedItem.getRepositoryData().getPath(), configInfo);
        new Thread(() -> {
            try {
                diffCommand.diff(selectedItem.getFileName());
            } catch (IOException | InterruptedException | GitConfigException ex) {
                showError(ex);
            }
        }).start();
    }

    private void gitDiffCached() {
        var selectedItem = getSelectedFile();
        GitDiffCommand diffCommand = gitCommandFactory.createGitDiffCommand(selectedItem.getRepositoryData().getPath(), configInfo);
        new Thread(() -> {
            try {
                diffCommand.diffCached(selectedItem.getFileName());
            } catch (IOException | InterruptedException | GitConfigException ex) {
                showError(ex);
            }
        }).start();
    }

    private void gitMergeTool() {
        var selectedItem = getSelectedFile();
        GitMergeToolCommand mergeToolCommand = new GitMergeToolCommand(selectedItem.getRepositoryData().getPath(), configInfo);
        new Thread(() -> {
            try {
                mergeToolCommand.exec(selectedItem.getFileName());

                GitStatusCommand statusCommand = new GitStatusCommand(selectedItem.getRepositoryData().getPath(), configInfo);
                List<GitStatusData> statusDatas = statusCommand.status(selectedItem.getRepositoryData(), selectedItem);
                Platform.runLater(() -> refreshFile(selectedItem, statusDatas));
            } catch (IOException | InterruptedException | GitConfigException ex) {
                showError(ex);
            }
        }).start();
    }

    private void gitCheckIgnore() {
        var selectedItem = getSelectedFile();
        GitCheckIgnoreCommand command = new GitCheckIgnoreCommand(selectedItem.getRepositoryData().getPath(), configInfo);
        String result;
        try {
            result = command.checkIgnore(selectedItem.getFileName());
        } catch (IOException | GitConfigException | InterruptedException ex) {
            errorLogWindow.appendException(ex);
            return;
        }
        informationLogWindow.appendText(result);
    }

    private void gitk(boolean isAll, boolean isSimlifyMerges) {
        var selectedItem = getSelectedFile();
        new Thread(() -> {
            try {
                GitkCommand gitkCommand = new GitkCommand(selectedItem.getRepositoryData().getPath(), configInfo);
                gitkCommand.setAllFlag(isAll);
                gitkCommand.setSimplifyMergesFlag(isSimlifyMerges);
                gitkCommand.log(selectedItem.getFileName());
            } catch (IOException | InterruptedException | GitConfigException ex) {
                showError(ex);
            }
        }).start();
    }

    private void deleteFile() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, datas) -> {
            for (var data : datas) {
                Files.delete(Paths.get(workDir.toString(), data.getFileName()));
            }
        });
    }

    private void copyFilePathToClipBoard() {
        if (tableView.getSelectionModel().getSelectedItems().size() != 1) {
            throw new AssertionError("ファイルは一つだけ選択されている時しか使用できません");
        }
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        GitStatusData statusData = tableView.getSelectionModel().getSelectedItems().get(0);
        Path path = Paths.get(statusData.getRepositoryData().getPath().toString(), statusData.getFileName());
        content.putString(path.toAbsolutePath().toString());
        clipboard.setContent(content);
    }

    private void openFileManager() {
        GitStatusData statusData = tableView.getSelectionModel().getSelectedItems().get(0);
        Path path = Paths.get(statusData.getRepositoryData().getPath().toString(), statusData.getFileName());
        FileBrowser.getInstance().browseFileDirectory(path);
    }

    private void execCommand(HashMap<Path, List<GitStatusData>> filesPerRepo, CommandExecutor2 command) {
        filesPerRepo.forEach((repositoryPath, items) -> {
            GitThread thread = GitThreadMan.get(repositoryPath.toString());
            thread.addCommand(() -> {
                Path workDir = repositoryPath;
                GitStatusCommand statusCommand = gitCommandFactory.createStatusCommand(workDir, configInfo);
                List<GitStatusData> statusDatas;
                try {
                    command.exec(workDir, items);
                    statusDatas = statusCommand.status(items.get(0).getRepositoryData(), items);
                } catch (IOException | GitConfigException | InterruptedException ex) {
                    showError(ex);
                    return;
                }
                Platform.runLater(() -> refreshFiles(items, statusDatas));
            });
        });
    }

    private void refreshFiles(List<GitStatusData> items, List<GitStatusData> statusDatas) {
        items.forEach(item -> refreshFileCommon(item, statusDatas));
        updateSituationSelectors();
    }

    private void refreshFile(GitStatusData item, List<GitStatusData> statusDatas) {
        refreshFileCommon(item, statusDatas);
        updateSituationSelectors();
    }

    private void refreshFileCommon(GitStatusData item, List<GitStatusData> statusDatas) {
        List<GitStatusData> newStatus = statusDatas.stream().filter(e -> e.getFileName().equals(item.getFileName())).collect(Collectors.toList());
        if (newStatus.isEmpty()) {
            item.getRepositoryData().getGitStatusDatas().remove(item);
            setRepositoryDisplayName(item.getRepositoryData());
        } else if (newStatus.size() == 1) {
            item.indexStatusProperty().set(newStatus.get(0).indexStatusProperty().get());
            item.workTreeStatusProperty().set(newStatus.get(0).workTreeStatusProperty().get());
        } else {
            throw new AssertionError(item.getFileName() + " が複数存在しています。");
        }
    }

    private void execCommandForRepository(Set<RepositoryData> repos, CommandExecutor2 command) {
        repos.forEach(repo -> {
            GitThread thread = GitThreadMan.get(repo.getPath().toString());
            thread.addCommand(() -> {
                Path workDir = repo.getPath();
                GitStatusCommand statusCommand = gitCommandFactory.createStatusCommand(workDir, configInfo);
                List<GitStatusData> statusDatas;
                try {
                    command.exec(workDir, null);
                } catch (IOException | GitConfigException | InterruptedException ex) {
                    showError(ex);
                    return;
                }
                Platform.runLater(() -> refreshRepository(repo, (gitStatusCommand, repository) -> gitStatusCommand.status(repository)));
            });
        });
    }

    private HashMap<Path, List<GitStatusData>> getSelectedFiles() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = new HashMap<>();
        tableView.getSelectionModel().getSelectedItems().forEach(item -> {
            Path key = item.getRepositoryData().getPath();
            List<GitStatusData> files = filesPerRepo.get(key);
            if (files == null) {
                files = new ArrayList<>();
                filesPerRepo.put(key, files);
            }
            files.add(item);
        });
        return filesPerRepo;
    }

    private GitStatusData getSelectedFile() {
        if (tableView.getSelectionModel().getSelectedIndices().size() != 1) {
            throw new AssertionError("複数選択されている場合又は選択されていない場合は getSelectedFile() は読んでは駄目");
        }
        return tableView.getSelectionModel().getSelectedItem();
    }

    // predicator で true になるファイルが存在する RepositoryData を返す。
    private HashSet<RepositoryData> getSpecifiedRepositories(Predicate<GitStatusData> predicator) {
        HashSet<RepositoryData> set = new HashSet<>();
        tableView.getItems().forEach(item -> {
            if (predicator.test(item)) {
                set.add(item.getRepositoryData());
            }
        });
        return set;
    }

    private void updateSituationSelectors() {
        singleSelectionSituationSelector.update();
        multiSelectionSituationSelector.update();
        gitAddSituationSelector.update();
        //gitAddSituationVisible.update();
        gitAddPatchSituationSelector.update();
        //gitAddSingleSituationVisible.update();
        gitAddUpdateSituationSelector.update();
        gitAddAllSituationSelector.update();
        gitUnstageSituationSelector.update();
        //gitUnstageSituationVisible.update();
        gitUnstageSingleSituationSelector.update();
        gitCheckoutHeadSituationSelector.update();
        //gitCheckoutHeadSituationVisible.update();
        gitCheckoutOursTheirsSituationSelector.update();
        gitCommitSituationSelector.update();
        gitRevertAbortSituationSelector.update();
        gitCherryPickAbortSituationSelector.update();
        gitRebaseAbortSituationSelector.update();
        gitMergeAbortSituationSelector.update();
        gitRebaseContinueSituationSelector.update();
        gitRebaseMenuSituationSelector.update();
        gitRmSituationSelector.update();
        gitCheckIgnoreSituationSelector.update();
        gitLogSituationSelector.update();
        gitMergeToolSituationSelector.update();
        gitDiffCachedSituationSelector.update();
        gitRestoreResetSituationSelector.update();
        deleteSituationSelector.update();

        // HierarchyMenuSelectionSituation はサブメニューの後に呼ばねばならない。
        gitDiffToolSituationSelector.update();
        gitAddMenuSituationSelector.update();
        gitRestoreMenuSituationSelector.update();
        gitCheckoutMenuSituationSelector.update();
    }

    private void showError(Exception e) {
        errorLogWindow.appendException(e);
    }
}
