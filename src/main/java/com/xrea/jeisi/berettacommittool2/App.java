package com.xrea.jeisi.berettacommittool2;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.execreator.ExeCreator;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusPane;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.preferencewindow.PreferenceWindow;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.selectworkpane.RepositoriesLoader;
import com.xrea.jeisi.berettacommittool2.selectworkpane.SelectWorkDialog;
import com.xrea.jeisi.berettacommittool2.selectworkpane.SelectWorkPane;
import com.xrea.jeisi.berettacommittool2.stylemanager.StyleManager;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
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
public class App extends Application {

    private final ErrorLogWindow errorLogWindow = new ErrorLogWindow();
    private StyleManager styleManager;
    private RepositoriesInfo repositoriesInfo;
    private RepositoriesPane repositoriesPane;
    private List<BaseGitPane> gitPanes;
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
        XmlWriter.writeStartMethod("App.start()");

        styleManager = new StyleManager(configInfo);
        
        mainStage = stage;
        loadConfig();
        var scene = buildScene(stage);
        styleManager.setRoot(scene.getRoot());
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
            setRootDirectory(directoryHistory.get(directoryHistory.size() - 1));
        }

        stage.show();

        try {
            ExeCreator.create(configInfo).exec();
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);
        }

        XmlWriter.writeEndMethod();
    }

    void close() {
        saveConfig();
        gitPanes.forEach(e -> e.close());
        GitThreadMan.closeAll();
        styleManager.close();
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
        XmlWriter.writeStartMethod("App.saveConfig()");
        try {
            var scene = mainStage.getScene();
            configInfo.setWindowRectangle("main", mainStage.getX(), mainStage.getY(), scene.getWidth(), scene.getHeight());
            configInfo.setDouble("main.splitpane.divider", splitPane.getDividerPositions()[0]);
            repositoriesPane.saveConfig();
            gitPanes.forEach((var pane) -> {
                pane.saveConfig();
            });
            configInfo.save();
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);
        } finally {
            XmlWriter.writeEndMethod();
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
        XmlWriter.writeStartMethod("App.buildScene()");

        repositoriesPane = new RepositoriesPane();
        repositoriesPane.setConfig(configInfo);

        gitPanes = new ArrayList<>();
        gitPanes.add(new GitStatusPane());
        var tabPane = new TabPane();
        gitPanes.forEach((var pane) -> {
            pane.setConfigInfo(configInfo);
            var tab = new Tab(pane.getTitle(), pane.build());
            tabPane.getTabs().add(tab);
        });

        splitPane = new SplitPane();
        var repositoriesPaneNode = repositoriesPane.build();
        splitPane.getItems().addAll(repositoriesPaneNode, tabPane);
        var divider = configInfo.getDouble("main.splitpane.divider");
        if (divider != null) {
            splitPane.setDividerPosition(0, divider);
        }
        SplitPane.setResizableWithParent(repositoriesPaneNode, Boolean.FALSE);

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
        //toolBar.getChildren().add(new Button("Refresh all"));
        //toolBar.getChildren().add(new Button("Commit"));

        var topPane = new VBox(menuBar, toolBar);

        BorderPane bodyBorderPane = new BorderPane();
        bodyBorderPane.setPadding(new Insets(5));
        //bodyBorderPane.setTop(selectWorkPane.build());
        bodyBorderPane.setCenter(splitPane);
        BorderPane.setMargin(splitPane, new Insets(5, 0, 0, 0));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topPane);
        borderPane.setCenter(bodyBorderPane);

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

        XmlWriter.writeEndMethod();
        return new Scene(borderPane, width, height);
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

    private void openPreference() {
        PreferenceWindow preferenceWindow = new PreferenceWindow(configInfo);
        preferenceWindow.open();
    }

    private void onChangeDirectory() {
        SelectWorkDialog dialog = new SelectWorkDialog(configInfo);
        SelectWorkPane selectWorkPane = dialog.getSelectWorkPane();
        var directoryHistory = configInfo.getDirectoryHistory();
        if (directoryHistory != null && directoryHistory.size() > 0) {
            selectWorkPane.setDirectoryHistory(directoryHistory);
        }
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedDirectory = selectWorkPane.getCurrentDirectory();
            setRootDirectory(selectedDirectory);
            System.out.println(selectWorkPane.getDirectoryHistory());
            configInfo.setDirectoryHistory(selectWorkPane.getDirectoryHistory());
        }
    }

    void setRootDirectory(String topDir) {
        this.topDir = topDir;
        refreshAll(topDir);
        //Platform.runLater(() -> {
        mainStage.setTitle(String.format("%s - %s", topDir, getBaseTitle()));
        //});
    }

    void refreshAll(String topDir) {
        //System.out.println("App.refreshAll(" + topDir + ")");
        if (topDir == null) {
            return;
        }

        //System.out.println("getDatas(): " + repositoriesPane.getTableView().getItems().toString());
        var loader = new RepositoriesLoader(Paths.get(topDir, ".git_repositories.lst"));
        //var loader = repositoriesLoaderFactory.create(Paths.get(topDir, ".git_repositories.lst"));
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
        gitPanes.forEach(pane -> {
            //pane.setRepositories(repositoriesInfo);
            pane.refreshAll();
        });

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

    private void refreshChecked() {
        gitPanes.forEach(pane -> {
            //pane.setRepositories(repositoriesInfo);
            pane.refreshChecked();
        });
    }

    private void refreshSelected() {
        gitPanes.forEach(pane -> pane.refreshSelected());
    }

    public static void main(String[] args) {
        launch();
    }
}
