/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.aggregatedobservablearraylist.AggregatedObservableArrayList;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.filebrowser.FileBrowser;
import com.xrea.jeisi.berettacommittool2.gitcommitwindow.GitCommitWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitAddCommand;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.filterpane.FilterPane;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCheckoutCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactory;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.gitthread.GitDiffCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitRmCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.gitthread.GitUnstageCommand;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddAllSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddPatchSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddPredicate;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddUpdatePredicate;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddUpdateSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCheckoutHeadSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCheckoutOursTheirsSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCommitSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitRemoveSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.HierarchyMenuSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.MultiSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SituationSelector;
//import com.xrea.jeisi.berettacommittool2.situationselector.SituationVisible;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

    private final ConfigInfo configInfo;
    private GitCommandFactory gitCommandFactory = new GitCommandFactoryImpl();
    private RepositoriesInfo repositories;
    private TableView<GitStatusData> tableView;
    private final FilterPane filterPane;
    private final AtomicInteger refreshThreadCounter = new AtomicInteger();
    private final ErrorLogWindow errorLogWindow;
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
    private final SituationSelector gitDiffToolSituationSelector = new SituationSelector();
    private final SituationSelector gitRmSituationSelector = new SituationSelector();
    private final TargetRepository targetRepository = TargetRepository.SELECTED;

    public GitStatusPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.progressWindow = new ProgressWindow(configInfo);
        this.errorLogWindow = new ErrorLogWindow(configInfo);
        this.filterPane = new FilterPane(configInfo, "gitstatuspane");
    }

    public int getRefreshThreadCounter() {
        return refreshThreadCounter.get();
    }

    @Override
    public String getTitle() {
        return "Status";
    }

    public void setGitCommandFactory(GitCommandFactory gitCommandFactory) {
        this.gitCommandFactory = gitCommandFactory;
    }

    @Override
    public void close() {
        errorLogWindow.close();
    }

    @Override
    public void setUp() {
        updateSituationSelectors();
    }

    @Override
    public void setRepositories(RepositoriesInfo work) {
        if (this.repositories != null) {
            throw new RuntimeException("setRepositories() を実行するのは一回だけです。");
        }

        this.repositories = work;

        ListChangeListener<RepositoryData> selectedListener = (change) -> {
            changeTargetRepositories(TargetRepository.SELECTED);
        };
        work.getSelected().addListener(selectedListener);
        ListChangeListener<RepositoryData> changedListener = (change) -> changeTargetRepositories(TargetRepository.CHECKED);
        work.getChecked().addListener(changedListener);

        var gitCommitSelectionSituation = new GitCommitSelectionSituation(repositories, targetRepository);
        gitCommitSituationSelector.setSituation(gitCommitSelectionSituation);

        changeTargetRepositories(targetRepository);
    }

    @Override
    public void saveConfig() {
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);

        filterPane.saveConfig();
    }

    private void changeTargetRepositories(TargetRepository target) {
        if (target != targetRepository) {
            return;
        }

        ObservableList<RepositoryData> targetRepositories = (targetRepository == TargetRepository.SELECTED) ? repositories.getSelected() : repositories.getChecked();
        AggregatedObservableArrayList aggregated = new AggregatedObservableArrayList();
        targetRepositories.forEach(e -> aggregated.appendList(e.getGitStatusDatas()));
        //tableView.setItems(aggregated.getAggregatedList());
        var filteredList = aggregated.getAggregatedList().filtered(null);
        tableView.setItems(filteredList);
        filterPane.setFilteredList(filteredList);
        updateSituationSelectors();
    }

    @Override
    public void refreshAll() {
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

    private void refreshCommon(ObservableList<RepositoryData> datas) {
        datas.forEach((var repository) -> {
            refreshRepository(repository);
        });
    }

    private void refreshRepository(RepositoryData repository) {
        refreshThreadCounter.incrementAndGet();
        repository.displayNameProperty().set(String.format("%s [updating...]", repository.nameProperty().get()));
        repository.getGitStatusDatas().clear();
        GitThread thread = GitThreadMan.get(repository.getPath().toString());
        thread.addCommand(() -> {
            GitStatusCommand command = gitCommandFactory.createStatusCommand(repository.getPath(), configInfo);
            List<GitStatusData> gitStatusDatas;
            try {
                gitStatusDatas = command.status(repository);
            } catch (IOException | GitConfigException | InterruptedException ex) {
                Platform.runLater(() -> showError(ex));
                repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getMessage()));
                return;
            }
            Platform.runLater(() -> {
                repository.getGitStatusDatas().setAll(gitStatusDatas);
                if (gitStatusDatas.isEmpty()) {
                    repository.displayNameProperty().set(String.format("%s", repository.nameProperty().get()));
                } else {
                    repository.displayNameProperty().set(String.format("%s (%d)", repository.nameProperty().get(), gitStatusDatas.size()));
                }
                updateSituationSelectors();
                refreshThreadCounter.decrementAndGet();
            });
        });
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
        add_uMenuItem.setId("gitStatusAddUpdateMenuItem");
        add_uMenuItem.setOnAction(eh -> gitAddUpdate());
        gitAddUpdateSituationSelector.getEnableMenuItems().add(add_uMenuItem);

        MenuItem addAllMenuItem = new MenuItem("git add -A");
        addAllMenuItem.setOnAction(eh -> gitAddAll());
        gitAddAllSituationSelector.getEnableMenuItems().add(addAllMenuItem);

        Menu addSubMenu = new Menu("git add");
        addSubMenu.getItems().addAll(addMenuItem, add_pMenuItem, add_uMenuItem, addAllMenuItem);
        gitAddMenuSituationSelector.setSituation(new HierarchyMenuSelectionSituation(addMenuItem, add_pMenuItem, add_uMenuItem, addAllMenuItem));
        gitAddMenuSituationSelector.getEnableMenuItems().add(addSubMenu);

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
        gitUnstageSingleSituationSelector.getEnableMenuItems().add(diffCachedMenuItem);

        Menu diffSubMenu = new Menu("git difftool");
        diffSubMenu.getItems().addAll(diffMenuItem, diffCachedMenuItem);
        gitDiffToolSituationSelector.setSituation(new HierarchyMenuSelectionSituation(diffMenuItem, diffCachedMenuItem));
        gitDiffToolSituationSelector.getEnableMenuItems().add(diffSubMenu);

        MenuItem commitMenuItem = new MenuItem("git commit");
        commitMenuItem.setId("gitStatusCommitMenuItem");
        commitMenuItem.setDisable(true);
        commitMenuItem.setOnAction(e -> gitCommit());
        commitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN));
        gitCommitSituationSelector.getEnableMenuItems().add(commitMenuItem);

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

        var menu = new Menu("Status");
        menu.setId("gitStatusMenu");
        menu.getItems().addAll(addSubMenu, checkoutSubMenu,
                unstageMenuItem, rmMenuItem, diffSubMenu, commitMenuItem, new SeparatorMenuItem(),
                filterMenuItem, copyFilePathMenuItem, openFileManagerMenuItem);
        return menu;
    }

    public Parent buildToolBar() {
        Button commitButton = new Button("Commit");
        commitButton.setTooltip(new Tooltip("git commit"));
        commitButton.setOnAction(eh -> gitCommit());
        gitCommitSituationSelector.getVisibleButotns().add(commitButton);

        Button addButton = new Button("+");
        addButton.setTooltip(new Tooltip("git add <file>..."));
        addButton.setOnAction(eh -> gitAdd());
        gitAddSituationSelector.getVisibleButotns().add(addButton);

        Button unstageButton = new Button("-");
        unstageButton.setTooltip(new Tooltip("git reset HEAD <file>..."));
        unstageButton.setOnAction(eh -> gitUnstage());
        gitUnstageSituationSelector.getVisibleButotns().add(unstageButton);

        Button checkoutHeadButton = new Button("checkout --");
        checkoutHeadButton.setTooltip(new Tooltip("git checkout -- <file>... (ローカルの編集の破棄)"));
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

        Button diffButton = new Button("Diff");
        diffButton.setTooltip(new Tooltip("git difftool <file>"));
        diffButton.setOnAction(eh -> gitDiff());
        gitAddPatchSituationSelector.getVisibleButotns().add(diffButton);

        Button diffCachedButton = new Button("Diff");
        diffCachedButton.setTooltip(new Tooltip("git difftool --cached <file>"));
        diffCachedButton.setOnAction(eh -> gitDiffCached());
        gitUnstageSingleSituationSelector.getVisibleButotns().add(diffCachedButton);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(commitButton, addButton, unstageButton, diffButton, diffCachedButton, checkoutHeadButton, checkoutTheirsButton, checkoutOursButton);
        //hbox.getChildren().addAll(addButton, commitButton);
        hbox.setSpacing(5);
        return hbox;
    }

    private ContextMenu buildContextMenu() {
        MenuItem checkoutOursMenuItem = new MenuItem("git checkout --ours <file>...");
        checkoutOursMenuItem.setOnAction(eh -> gitCheckoutOurs());
        gitCheckoutOursTheirsSituationSelector.getVisibleMenuItems().add(checkoutOursMenuItem);

        MenuItem checkoutTheirsMenuItem = new MenuItem("git checkout --theirs <file>...");
        checkoutTheirsMenuItem.setOnAction(eh -> gitCheckoutTheirs());
        gitCheckoutOursTheirsSituationSelector.getVisibleMenuItems().add(checkoutTheirsMenuItem);

        MenuItem copyFilePathMenuItem = new MenuItem("ファイルのフルパスをコピー");
        copyFilePathMenuItem.setOnAction(eh -> copyFilePathToClipBoard());
        singleSelectionSituationSelector.getEnableMenuItems().add(copyFilePathMenuItem);

        MenuItem openFileManagerMenuItem = createOpenFileManagerMenuItem();

        ContextMenu contextMenu = new ContextMenu(checkoutOursMenuItem, checkoutTheirsMenuItem, copyFilePathMenuItem, openFileManagerMenuItem);
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
        GitCommitWindow commitWindow = new GitCommitWindow(configInfo);
        commitWindow.getGitCommitPane().setGitCommandFactory(gitCommandFactory);
        commitWindow.open();
        commitWindow.getGitCommitPane().setRepositoryDatas(repositories.getSelected());
        commitWindow.getGitCommitPane().addEventHandler((e) -> refreshSelected());
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
        XmlWriter.writeStartMethod("GitStatusPane.gitDiff()");
        var selectedItem = getSelectedFile();
        GitDiffCommand diffCommand = gitCommandFactory.createGitDiffCommand(selectedItem.getRepositoryData().getPath(), configInfo);
        new Thread(() -> {
            try {
                diffCommand.diff(selectedItem.getFileName());
            } catch (IOException | InterruptedException | GitConfigException ex) {
                showError(ex);
            }
        }).start();
        XmlWriter.writeEndMethod();
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
                    //List<String> filesList = items.stream().map(e -> e.fileNameProperty().get()).collect(Collectors.toList());
                    //String[] files = filesList.toArray(new String[filesList.size()]);
                    command.exec(workDir, items);
                    statusDatas = statusCommand.status(items.get(0).getRepositoryData(), items);
                } catch (IOException | GitConfigException | InterruptedException ex) {
                    showError(ex);
                    return;
                }
                Platform.runLater(() -> {
                    items.forEach(item -> {
                        List<GitStatusData> newStatus = statusDatas.stream().filter(e -> e.getFileName().equals(item.getFileName())).collect(Collectors.toList());
                        if (newStatus.isEmpty()) {
                            item.getRepositoryData().getGitStatusDatas().remove(item);
                        } else if (newStatus.size() == 1) {
                            item.indexStatusProperty().set(newStatus.get(0).indexStatusProperty().get());
                            item.workTreeStatusProperty().set(newStatus.get(0).workTreeStatusProperty().get());
                        } else {
                            throw new AssertionError(item.getFileName() + " が複数存在しています。");
                        }
                    });
                    updateSituationSelectors();
                });
            });
        });
    }

    private void execCommandForRepository(Set<RepositoryData> repos, CommandExecutor command) {
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
                Platform.runLater(() -> refreshRepository(repo));
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

    /*
    private HashMap<Path, List<GitStatusData>> getModifiedFiles() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = new HashMap<>();
        tableView.getEnableMenuItems().forEach(item -> {
            switch (item.getWorkTreeStatus()) {
                case "M":
                case "A":
                case "D":
                    break;
                default:
                    return;
            }
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
     */
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
        //gitUnstageSingleSituationVisible.update();
        gitCommitSituationSelector.update();
        //gitCommitSituationVisible.update();
        gitRmSituationSelector.update();

        // HierarchyMenuSelectionSituation はサブメニューの後に呼ばねばならない。
        gitDiffToolSituationSelector.update();
        gitAddMenuSituationSelector.update();
        gitCheckoutMenuSituationSelector.update();
    }

    private void showError(Exception e) {
        errorLogWindow.appendException(e);
    }
}
