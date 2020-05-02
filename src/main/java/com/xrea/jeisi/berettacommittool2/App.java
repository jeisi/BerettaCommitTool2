package com.xrea.jeisi.berettacommittool2;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusPane;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.selectworkpane.RepositoriesLoader;
import com.xrea.jeisi.berettacommittool2.selectworkpane.SelectWorkPane;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private RepositoriesInfo repositoriesInfo;
    private RepositoriesPane repositoriesPane;
    private SelectWorkPane selectWorkPane;
    private List<BaseGitPane> gitPanes;
    private final ConfigInfo configInfo = new ConfigInfo();
    private String topDir;
    //private RepositoriesLoaderFactory repositoriesLoaderFactory = (file) -> new RepositoriesLoader(file);

    @Override
    public void start(Stage stage) {
        var scene = buildScene(stage);
        stage.setScene(scene);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
                gitPanes.forEach(e -> e.close());
            }
        });
        stage.show();

        setupRepositoriesInfo();
        loadConfig();
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
            configInfo.setDirectoryHistory(selectWorkPane.getDirectoryHistory());
            configInfo.save();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(AlertType.ERROR, ex.getLocalizedMessage(), ButtonType.CLOSE);
        }
    }

    private void loadConfig() {
        try {
            configInfo.load();
            var directoryHistory = configInfo.getDirectoryHistory();
            if (directoryHistory != null && directoryHistory.size() > 0) {
                selectWorkPane.setDirectoryHistory(directoryHistory);
            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(AlertType.ERROR, ex.getLocalizedMessage(), ButtonType.CLOSE);
        }
    }

    Scene buildScene(Stage stage) {
        selectWorkPane = new SelectWorkPane(stage);
        selectWorkPane.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selectedDirectory = ((ComboBox<String>) e.getSource()).getValue();
                setRootDirectory(selectedDirectory);
            }
        });

        repositoriesPane = new RepositoriesPane();

        gitPanes = new ArrayList<>();
        gitPanes.add(new GitStatusPane());
        var tabPane = new TabPane();
        gitPanes.forEach((var pane) -> {
            var tab = new Tab(pane.getTitle(), pane.build());
            tabPane.getTabs().add(tab);
        });

        var splitPane = new SplitPane();
        splitPane.getItems().addAll(repositoriesPane.build(), tabPane);

        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().add(buildMenu());
        menuBar.getMenus().add(repositoriesPane.buildMenu());
        gitPanes.forEach(pane -> menuBar.getMenus().add(pane.buildMenu()));

        var vbox = new VBox();
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        vbox.getChildren().addAll(selectWorkPane.build(), splitPane);

        var borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(vbox);

        return new Scene(borderPane, 640, 480);
    }

    private Menu buildMenu() {
        var refreshAllMenuItem = new MenuItem("Refresh all");
        refreshAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        refreshAllMenuItem.setOnAction(e -> refreshAll(topDir));

        var refreshCheckedMenuItem = new MenuItem("Refresh checked");
        refreshCheckedMenuItem.setOnAction(e -> refreshChecked());
        
        var refreshSelectedMenuItem = new MenuItem("Refresh selected");
        refreshSelectedMenuItem.setOnAction(e -> refreshSelected());
        
        Menu menu = new Menu("Refresh");
        menu.getItems().addAll(refreshAllMenuItem, refreshCheckedMenuItem, refreshSelectedMenuItem);
        
        return menu;
    }

    void setRootDirectory(String topDir) {
        this.topDir = topDir;
        refreshAll(topDir);
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
            if(uncheckedItems.contains(item.getPath())) {
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
