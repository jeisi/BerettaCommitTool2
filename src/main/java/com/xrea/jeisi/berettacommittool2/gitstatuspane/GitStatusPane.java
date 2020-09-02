/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitstatuspane;

import com.xrea.jeisi.berettacommittool2.aggregatedobservablearraylist.AggregatedObservableArrayList;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.gitcommitwindow.GitCommitWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitAddCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandException;
import com.xrea.jeisi.berettacommittool2.gitthread.GitStatusCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactory;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.gitthread.GitDiffCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.gitthread.GitUnstageCommand;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddPatchSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitAddUpdateSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitCommitSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.GitUnstageSingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.MultiSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SingleSelectionSituation;
import com.xrea.jeisi.berettacommittool2.situationselector.SituationSelector;
import com.xrea.jeisi.berettacommittool2.situationselector.SituationVisible;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 *
 * @author jeisi
 */
public class GitStatusPane implements BaseGitPane {

    private final ErrorLogWindow errorLogWindow = new ErrorLogWindow();
    private GitCommandFactory gitCommandFactory = new GitCommandFactoryImpl();
    private RepositoriesInfo repositories;
    private TableView<GitStatusData> tableView;
    private final TargetRepository targetRepository = TargetRepository.SELECTED;
    private final SituationSelector singleSelectionSituationSelector = new SituationSelector();
    private final SituationSelector multiSelectionSituationSelector = new SituationSelector();
    private final SituationSelector gitAddSituationSelector = new SituationSelector();
    private final SituationSelector gitAddPatchSituationSelector = new SituationSelector();
    private final SituationSelector gitAddUpdateSituationSelector = new SituationSelector();
    private final SituationSelector gitCommitSituationSelector = new SituationSelector();
    private final SituationSelector gitUnstageSituationSelector = new SituationSelector();
    private final SituationSelector gitUnstageSingleSituationSelector = new SituationSelector();
    private final SituationVisible gitAddSituationVisible = new SituationVisible();
    private final SituationVisible gitAddSingleSituationVisible = new SituationVisible();
    private final SituationVisible gitCommitSituationVisible = new SituationVisible();
    private final SituationVisible gitUnstageSituationVisible = new SituationVisible();
    private final SituationVisible gitUnstageSingleSituationVisible = new SituationVisible();
    private final ProgressWindow progressWindow = new ProgressWindow();
    private ConfigInfo configInfo;

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
            //System.out.println("selectedListener()");
            changeTargetRepositories(TargetRepository.SELECTED);
        };
        work.getSelected().addListener(selectedListener);
        ListChangeListener<RepositoryData> changedListener = (change) -> changeTargetRepositories(TargetRepository.CHECKED);
        work.getChecked().addListener(changedListener);

        changeTargetRepositories(targetRepository);
    }

    @Override
    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public void saveConfig() {
        XmlWriter.writeStartMethod("GitStatusPane.saveConfig()");
        
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);
        
        XmlWriter.writeEndMethod();
    }

    private void changeTargetRepositories(TargetRepository target) {
        XmlWriter.writeStartMethod("GitStatusPane.changeTargetRepositories()");
        if (target != targetRepository) {
            XmlWriter.writeEndMethodWithReturn();
            return;
        }

        ObservableList<RepositoryData> targetRepositories = (targetRepository == TargetRepository.SELECTED) ? repositories.getSelected() : repositories.getChecked();
        AggregatedObservableArrayList aggregated = new AggregatedObservableArrayList();
        targetRepositories.forEach(e -> aggregated.appendList(e.getGitStatusDatas()));
        XmlWriter.writeObject("aggregated.getAggregatedList()", aggregated.getAggregatedList());
        tableView.setItems(aggregated.getAggregatedList());
        updateSituationSelectors();
        XmlWriter.writeEndMethod();
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
        //System.out.println("refreshCommon(): " + datas.toString());
        datas.forEach((var repository) -> {
            repository.displayNameProperty().set(String.format("%s [updating...]", repository.nameProperty().get()));
            repository.getGitStatusDatas().clear();
            GitThread thread = GitThreadMan.get(repository.getPath().toString());
            thread.addCommand(() -> {
                GitStatusCommand command = gitCommandFactory.createStatusCommand(repository.getPath().toFile());
                List<GitStatusData> gitStatusDatas;
                try {
                    gitStatusDatas = command.status(repository);
                    //System.out.println("gitStatusDatas: " + gitStatusDatas.toString());
                } catch (IOException | GitAPIException ex) {
                    Platform.runLater(() -> showError(ex));
                    repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getMessage()));
                    return;
                }
                Platform.runLater(() -> {
                    //System.out.println(String.format("[%s].getGitStatusDatas().setAll(gitStatusDatas)", repository.nameProperty().get()));
                    repository.getGitStatusDatas().setAll(gitStatusDatas);
                    if (gitStatusDatas.size() == 0) {
                        repository.displayNameProperty().set(String.format("%s", repository.nameProperty().get()));
                    } else {
                        repository.displayNameProperty().set(String.format("%s (%d)", repository.nameProperty().get(), gitStatusDatas.size()));
                    }
                });
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
        gitAddSituationVisible.setSituation(gitAddSelectionSituation);
        var gitAddSingleSelectionSituation = new GitAddPatchSelectionSituation(selectionModel);
        gitAddPatchSituationSelector.setSituation(gitAddSingleSelectionSituation);
        gitAddSingleSituationVisible.setSituation(gitAddSingleSelectionSituation);
        gitAddUpdateSituationSelector.setSituation(new GitAddUpdateSelectionSituation(tableView));
        var gitUnstageSelectionSituation = new GitUnstageSelectionSituation(selectionModel);
        gitUnstageSituationSelector.setSituation(gitUnstageSelectionSituation);
        gitUnstageSituationVisible.setSituation(gitUnstageSelectionSituation);
        var gitUnstageSingleSelectionSituation = new GitUnstageSingleSelectionSituation(selectionModel);
        gitUnstageSingleSituationSelector.setSituation(gitUnstageSingleSelectionSituation);
        gitUnstageSingleSituationVisible.setSituation(gitUnstageSingleSelectionSituation);
        var gitCommitSelectionSituation = new GitCommitSelectionSituation(tableView);
        gitCommitSituationSelector.setSituation(gitCommitSelectionSituation);
        gitCommitSituationVisible.setSituation(gitCommitSelectionSituation);

        tableView.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Integer> change) {
                updateSituationSelectors();
            }
        });

        //var vbox = new VBox();
        //vbox.getChildren().addAll(tableView);
        return tableView;
    }

    public Menu buildMenu() {
        MenuItem addMenuItem = new MenuItem("Git add <file>...");
        addMenuItem.setId("gitStatusAddMenuItem");
        addMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        addMenuItem.setOnAction(e -> gitAdd());
        gitAddSituationSelector.getItems().add(addMenuItem);

        MenuItem add_pMenuItem = new MenuItem("Git add -p <file>");
        add_pMenuItem.setId("gitStatusAddpMenuItem");
        gitAddPatchSituationSelector.getItems().add(add_pMenuItem);
        // TODO: 選択できる条件はほぼ git add と同じだが、厳密にはまだブランチがない時は選択不可。

        MenuItem add_uMenuItem = new MenuItem("Git add -u");
        add_uMenuItem.setId("gitStatusAddUpdateMenuItem");
        add_uMenuItem.setOnAction(eh -> gitAddUpdate());
        gitAddUpdateSituationSelector.getItems().add(add_uMenuItem);

        MenuItem unstageMenuItem = new MenuItem("Git reset HEAD <file>...");
        unstageMenuItem.setId("gitStatusUnstageMenuItem");
        unstageMenuItem.setOnAction(e -> gitUnstage());
        gitUnstageSituationSelector.getItems().add(unstageMenuItem);

        MenuItem diffMenuItem = new MenuItem("Git difftool <file>");
        diffMenuItem.setId("gitStatusDiffMenuItem");
        diffMenuItem.setOnAction(eh -> gitDiff());
        diffMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        gitAddPatchSituationSelector.getItems().add(diffMenuItem);

        MenuItem diffCachedMenuItem = new MenuItem("Git difftool --cached <file>");
        diffCachedMenuItem.setId("gitStatusDiffCachedMenuItem");
        diffCachedMenuItem.setOnAction(eh -> gitDiffCached());
        diffCachedMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        gitUnstageSingleSituationSelector.getItems().add(diffCachedMenuItem);

        MenuItem commitMenuItem = new MenuItem("Git commit");
        commitMenuItem.setId("gitStatusCommitMenuItem");
        commitMenuItem.setDisable(true);
        commitMenuItem.setOnAction(e -> gitCommit());
        gitCommitSituationSelector.getItems().add(commitMenuItem);

        var menu = new Menu("Status");
        menu.setId("gitStatusMenu");
        menu.getItems().addAll(addMenuItem, add_pMenuItem, add_uMenuItem, new SeparatorMenuItem(),
                unstageMenuItem, diffMenuItem, diffCachedMenuItem, commitMenuItem);
        return menu;
    }

    public Parent buildToolBar() {
        Button commitButton = new Button("Commit");
        commitButton.setTooltip(new Tooltip("git commit"));
        commitButton.setOnAction(eh -> gitCommit());
        gitCommitSituationVisible.getItems().add(commitButton);

        Button addButton = new Button("+");
        addButton.setTooltip(new Tooltip("git add <file>..."));
        addButton.setOnAction(eh -> gitAdd());
        gitAddSituationVisible.getItems().add(addButton);

        Button unstageButton = new Button("-");
        unstageButton.setTooltip(new Tooltip("git reset HEAD <file>..."));
        unstageButton.setOnAction(eh -> gitUnstage());
        gitUnstageSituationVisible.getItems().add(unstageButton);

        Button diffButton = new Button("Diff");
        diffButton.setTooltip(new Tooltip("git diff <file>"));
        diffButton.setOnAction(eh -> gitDiff());
        gitAddSingleSituationVisible.getItems().add(diffButton);

        Button diffCachedButton = new Button("Diff");
        diffCachedButton.setTooltip(new Tooltip("git diff --cached <file>"));
        diffCachedButton.setOnAction(eh -> gitDiffCached());
        gitUnstageSingleSituationVisible.getItems().add(diffCachedButton);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(commitButton, addButton, unstageButton, diffButton, diffCachedButton);
        //hbox.getChildren().addAll(addButton, commitButton);
        hbox.setSpacing(5);
        return hbox;
    }

    private void gitCommit() {
        GitCommitWindow commitWindow = new GitCommitWindow();
        commitWindow.getGitCommitPane().setGitCommandFactory(gitCommandFactory);
        commitWindow.setConfigInfo(configInfo);
        commitWindow.open();
        commitWindow.getGitCommitPane().setRepositoryDatas(repositories.getSelected());
        commitWindow.getGitCommitPane().addEventHandler((e) -> refreshSelected());
    }

    private void gitAdd() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, files) -> {
            GitAddCommand addCommand = gitCommandFactory.createAddCommand(workDir);
            addCommand.setProgressWindow(progressWindow);
            addCommand.add(files);
        });
    }

    // git add -u 相当
    private void gitAddUpdate() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getModifiedFiles();
        execCommand(filesPerRepo, (workDir, files) -> {
            GitAddCommand addCommand = gitCommandFactory.createAddCommand(workDir);
            addCommand.setProgressWindow(progressWindow);
            addCommand.add(files);
        });
    }

    private void gitUnstage() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = getSelectedFiles();
        execCommand(filesPerRepo, (workDir, files) -> {
            GitUnstageCommand unstageCommand = gitCommandFactory.createUnstageCommand(workDir);
            unstageCommand.setProgressWindow(progressWindow);
            unstageCommand.unstage(files);
        });
    }

    private void gitDiff() {
        var selectedItem = getSelectedFile();
        GitDiffCommand diffCommand = gitCommandFactory.createGitDiffCommand(selectedItem.getRepositoryData().getPath().toFile());
        try {
            diffCommand.diff(selectedItem.getFileName());
        } catch (IOException | GitCommandException | InterruptedException ex) {
            showError(ex);
        }
    }

    private void gitDiffCached() {
        var selectedItem = getSelectedFile();
        GitDiffCommand diffCommand = gitCommandFactory.createGitDiffCommand(selectedItem.getRepositoryData().getPath().toFile());
        try {
            diffCommand.diffCached(selectedItem.getFileName());
        } catch (IOException | GitCommandException | InterruptedException ex) {
            showError(ex);
        }
    }

    private void execCommand(HashMap<Path, List<GitStatusData>> filesPerRepo, CommandExecutor command) {
        filesPerRepo.forEach((repositoryPath, items) -> {
            GitThread thread = GitThreadMan.get(repositoryPath.toString());
            thread.addCommand(() -> {
                File workDir = repositoryPath.toFile();
                GitStatusCommand statusCommand = gitCommandFactory.createStatusCommand(workDir);
                //GitAddCommand unstageCommand = gitCommandFactory.createAddCommand(workDir);
                List<GitStatusData> statusDatas;
                try {
                    List<String> filesList = items.stream().map(e -> e.fileNameProperty().get()).collect(Collectors.toList());
                    String[] files = filesList.toArray(new String[filesList.size()]);
                    command.exec(workDir, files);
                    statusDatas = statusCommand.status(items.get(0).getRepositoryData(), files);
                    //System.out.println("statusDatas: " + statusDatas.toString());
                } catch (IOException | GitAPIException ex) {
                    showError(ex);
                    return;
                }
                Platform.runLater(() -> {
                    //System.out.println("items: " + items.toString());
                    items.forEach(item -> {
                        List<GitStatusData> newStatus = statusDatas.stream().filter(e -> e.getFileName().equals(item.getFileName())).collect(Collectors.toList());
                        if (newStatus.isEmpty()) {
                            //System.out.println("tableView.getItems(): " + tableView.getItems().toString());
                            //tableView.getItems().remove(item);
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

    private HashMap<Path, List<GitStatusData>> getModifiedFiles() {
        HashMap<Path, List<GitStatusData>> filesPerRepo = new HashMap<>();
        tableView.getItems().forEach(item -> {
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

    private void updateSituationSelectors() {
        singleSelectionSituationSelector.update();
        multiSelectionSituationSelector.update();
        gitAddSituationSelector.update();
        gitAddSituationVisible.update();
        gitAddPatchSituationSelector.update();
        gitAddSingleSituationVisible.update();
        gitAddUpdateSituationSelector.update();
        gitUnstageSituationSelector.update();
        gitUnstageSituationVisible.update();
        gitUnstageSingleSituationSelector.update();
        gitUnstageSingleSituationVisible.update();
        gitCommitSituationSelector.update();
        gitCommitSituationVisible.update();
    }

    private void showError(Exception e) {
        if (configInfo != null) {
            errorLogWindow.setConfigInfo("gitstatuspane.logwindow", configInfo);
        }
        errorLogWindow.appendException(e);
    }
}
