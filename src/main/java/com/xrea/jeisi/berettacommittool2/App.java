package com.xrea.jeisi.berettacommittool2;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.execreator.ExeCreator;
import com.xrea.jeisi.berettacommittool2.basegitpane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.basegitpane.RefreshListener;
import com.xrea.jeisi.berettacommittool2.configinfo.StageSizeManager;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitbranchpane.GitBranchPane;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusPane;
import com.xrea.jeisi.berettacommittool2.gitsyncbranch.GitSyncPane;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.preferencewindow.PreferenceWindow;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.selectworkpane.RepositoriesLoader;
import com.xrea.jeisi.berettacommittool2.selectworkpane.SelectWorkDialog2;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import com.xrea.jeisi.berettacommittool2.targetrepositorypane.TargetRepositoryPane;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application implements RefreshListener {

    private ErrorLogWindow errorLogWindow;
    private List<BaseGitPane> gitPanes;
    private StyleManager styleManager;
    private PreferenceWindow preferenceWindow;
    private RepositoriesInfo repositoriesInfo;
    private RepositoriesPane repositoriesPane;
    private TabPane tabPane;
    private TargetRepositoryPane targetRepositoryPane;
    ConfigInfo configInfo = new ConfigInfo();
    private String topDir;
    Stage mainStage;
    SplitPane splitPane;
    //private BorderPane borderPane;

    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public void start(Stage stage) {
        styleManager = new StyleManager(configInfo);
        errorLogWindow = new ErrorLogWindow(configInfo);
        preferenceWindow = new PreferenceWindow(configInfo);
        configInfo.setMainApp(this);

        StageSizeManager.setUp();
        
        mainStage = stage;
        loadConfig();
        var scene = buildScene(stage);
        stage.setScene(scene);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                close();
            }
        });
        stage.setTitle(getBaseTitle());

        setupRepositoriesInfo();

        var directoryHistory = configInfo.getDirectoryHistory();
        if (directoryHistory != null && directoryHistory.size() > 0) {
            setRootDirectory(directoryHistory.get(0));
        }

        styleManager.setStage(stage);
        stage.show();

        try {
            ExeCreator.create(configInfo).exec();
        } catch (IOException | InterruptedException | GitConfigException ex) {
            errorLogWindow.appendException(ex);
        }
    }

    void close() {
        preferenceWindow.close();
        saveConfig();
        gitPanes.forEach(e -> e.close());
        GitThreadMan.closeAll();
    }

    private static String getBaseTitle() {
        return "BerettaCommitTool2";
    }

    public void setupRepositoriesInfo() {
        repositoriesInfo = new RepositoriesInfo(repositoriesPane.getTableView());
        repositoriesPane.setRepositories(repositoriesInfo);
        gitPanes.forEach(pane -> {
            pane.setUp();
            pane.setRepositories(repositoriesInfo);
        });
    }

    private void saveConfig() {
        try {
            var scene = mainStage.getScene();
            //configInfo.setWindowRectangle("main", mainStage.getX(), mainStage.getY(), scene.getWidth(), scene.getHeight());
            configInfo.setDouble("main.splitpane.divider", splitPane.getDividerPositions()[0]);
            repositoriesPane.saveConfig();
            gitPanes.forEach((var pane) -> {
                pane.saveConfig();
            });
            configInfo.save();
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);
        }
    }

    private void loadConfig() {
        try {
            configInfo.load();
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);
        }
    }

    Scene buildScene(Stage stage) {
        repositoriesPane = new RepositoriesPane();
        repositoriesPane.setConfig(configInfo);
        repositoriesPane.setErrorLogWindow(errorLogWindow);
        repositoriesPane.setRefreshListener(this);

        gitPanes = new ArrayList<>();
        gitPanes.add(new GitStatusPane(configInfo));
        gitPanes.add(new GitSyncPane(configInfo));
        gitPanes.add(new GitBranchPane(configInfo));
        tabPane = new TabPane();
        gitPanes.forEach((var pane) -> {
            var tab = new Tab(pane.getTitle(), pane.build());
            tab.setUserData(pane);
            tabPane.getTabs().add(tab);
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ((BaseGitPane) oldValue.getUserData()).setActive(false);
            BaseGitPane pane = (BaseGitPane) newValue.getUserData();
            targetRepositoryPane.bind(pane.targetRepositoryProperty());
            pane.setActive(true);
        });

        targetRepositoryPane = new TargetRepositoryPane();
        BorderPane leftPane = new BorderPane();
        leftPane.setCenter(repositoriesPane.build());
        leftPane.setBottom(targetRepositoryPane.build());

        splitPane = new SplitPane();
        splitPane.getItems().addAll(leftPane, tabPane);
        var divider = configInfo.getDouble("main.splitpane.divider");
        if (divider != null) {
            splitPane.setDividerPosition(0, divider);
        }
        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);

        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().add(buildMenu());
        menuBar.getMenus().add(repositoriesPane.buildMenu());
        gitPanes.forEach(pane -> menuBar.getMenus().add(pane.buildMenu()));
        menuBar.getMenus().add(buildToolsMenu());

        //var shortCutButtons = new HBox();
        var toolBar = new ToolBar();
        //toolBar.setSpacing(5);
        Button changeDirectoryButton = new Button("Change directory");
        changeDirectoryButton.setOnAction(eh -> onChangeDirectory());
        Button refreshAllButton = new Button("Refresh all");
        refreshAllButton.setOnAction(eh -> refreshAll(topDir));
        toolBar.getItems().addAll(changeDirectoryButton, refreshAllButton);
        gitPanes.forEach(pane -> toolBar.getItems().add(pane.buildToolBar()));

        var topPane = new VBox(menuBar, toolBar);

        BorderPane bodyBorderPane = new BorderPane();
        bodyBorderPane.setPadding(new Insets(5));
        //bodyBorderPane.setTop(selectWorkPane.build());
        bodyBorderPane.setCenter(splitPane);
        BorderPane.setMargin(splitPane, new Insets(5, 0, 0, 0));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topPane);
        borderPane.setCenter(bodyBorderPane);

        /*
        var windowRectangle = configInfo.getWindowRectangle("main");
        double width, height;
        if (windowRectangle != null) {
            mainStage.setX(windowRectangle.getX());
            mainStage.setY(windowRectangle.getY());
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
        } else {
            width = 640;
            height = 480;
        }
        */
        return StageSizeManager.build(mainStage, configInfo, borderPane, "main", 640, 480);
    }

    private Menu buildMenu() {
        var changeDirectoryMenuItem = new MenuItem("Change directory");
        changeDirectoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        changeDirectoryMenuItem.setOnAction(eh -> onChangeDirectory());

        var refreshAllMenuItem = new MenuItem("Refresh all");
        refreshAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        refreshAllMenuItem.setOnAction(e -> refreshAll(topDir));

        var refreshCheckedMenuItem = new MenuItem("Refresh checked");
        refreshCheckedMenuItem.setOnAction(e -> refreshChecked());

        var refreshSelectedMenuItem = new MenuItem("Refresh selected");
        refreshSelectedMenuItem.setOnAction(e -> refreshSelected());

        Menu menu = new Menu("Refresh");
        menu.getItems().addAll(changeDirectoryMenuItem, refreshAllMenuItem, refreshCheckedMenuItem, refreshSelectedMenuItem);

        return menu;
    }

    private Menu buildToolsMenu() {
        MenuItem preferenceMenuItem = new MenuItem("Preference");
        preferenceMenuItem.setOnAction(eh -> openPreference());

        Menu menu = new Menu("Tools");
        menu.getItems().addAll(preferenceMenuItem);
        return menu;
    }

    public void openPreference() {
        preferenceWindow.open();
    }

    public void openPreference(String defaultTab) {
        if(preferenceWindow == null) {
            return;
        }
        
        preferenceWindow.open(defaultTab);
    }

    private void onChangeDirectory() {
        SelectWorkDialog2 dialog = new SelectWorkDialog2(configInfo);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedDirectory = dialog.getCurrentDirectory();
            setRootDirectory(selectedDirectory);
        }
    }

    void setRootDirectory(String topDir) {
        XmlWriter.writeStartMethod("App.setRootDirectory()");
        this.topDir = topDir;
        refreshAll(topDir);
        //((BaseGitPane)tabPane.getSelectionModel().getSelectedItem().getUserData()).setActive(true);
        mainStage.setTitle(String.format("%s - %s", topDir, getBaseTitle()));
        XmlWriter.writeEndMethod();
    }

    void refreshAll(String topDir) {
        if (topDir == null) {
            return;
        }

        var loader = new RepositoriesLoader(Paths.get(topDir, ".git_repositories.lst"));
        List<String> repositories;

        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, ".git_repositories.lst 読み込み中にエラーが発生しました。", ButtonType.CLOSE);
            alert.showAndWait();
            return;
        }
        repositories = loader.getLines();

        //List<Path> selectedItems = repositoriesPane.getTableView().getSelectionModel().getSelectedItems()
        //        .stream().map(e -> e.getPath()).collect(Collectors.toList());
        List<Path> selectedItems = repositoriesInfo.getSelected().stream().map(e -> e.getPath()).collect(Collectors.toList());
        //List<Path> checkedItems = repositoriesInfo.getChecked().stream().map(e -> e.getPath()).collect(Collectors.toList());
        List<Path> uncheckedItems = repositoriesInfo.getDatas().stream().filter(e -> !e.checkProperty().get()).map(e -> e.getPath()).collect(Collectors.toList());
        repositoriesInfo.setRepositories(repositories, topDir);
//        gitPanes.forEach(pane -> {
//            pane.refreshAll();
//        });
        BaseGitPane pane = (BaseGitPane) tabPane.getSelectionModel().getSelectedItem().getUserData();
        pane.refreshAll();

        // 元々選択されていた行を選択し直す。
        int select = -1;
        List<Integer> selectedIndicesList = new ArrayList<>();
        var items = repositoriesPane.getTableView().getItems();
        for (int i = 0; i < items.size(); ++i) {
            if (selectedItems.contains(items.get(i).getPath())) {
                if (select == -1) {
                    select = i;
                } else {
                    selectedIndicesList.add(i);
                }
            }
        }
        int[] selectedIndices = new int[selectedIndicesList.size()];
        for (int i = 0; i < selectedIndicesList.size(); ++i) {
            selectedIndices[i] = selectedIndicesList.get(i);
            //System.out.println(String.format("selectedIndices[%d]: %d", i, selectedIndices[i]));
        }
        if (select != -1) {
            repositoriesPane.getTableView().getSelectionModel().selectIndices(select, selectedIndices);
        }

        // 元々チェックされてなかった行に対してチェックを外す。
        repositoriesPane.getTableView().getItems().forEach(item -> {
            if (uncheckedItems.contains(item.getPath())) {
                item.checkProperty().set(false);
            }
        });

    }

    @Override
    public void refreshAll() {
        refreshAll(topDir);
    }
    
    @Override
    public void refreshChecked() {
//        gitPanes.forEach(pane -> {
//            pane.refreshChecked();
//        });
        BaseGitPane pane = (BaseGitPane) tabPane.getSelectionModel().getSelectedItem().getUserData();
        pane.refreshChecked();;
    }

    @Override
    public void refreshSelected() {
//        gitPanes.forEach(pane -> pane.refreshSelected());
        BaseGitPane pane = (BaseGitPane) tabPane.getSelectionModel().getSelectedItem().getUserData();
        pane.refreshSelected();;
    }

    public static void main(String[] args) {
        launch();
    }
}
