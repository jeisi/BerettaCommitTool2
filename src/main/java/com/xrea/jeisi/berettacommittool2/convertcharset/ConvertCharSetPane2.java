/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.aggregatedobservablearraylist.AggregatedObservableArrayList;
import com.xrea.jeisi.berettacommittool2.basegitpane.BaseGitPane;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.filterpane.GitStatusDataFilterPane;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.LogWriter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author jeisi
 */
public class ConvertCharSetPane2 implements BaseGitPane {

    private boolean active = false;
    private RepositoriesInfo repositories;
    private TableView<GitStatusData> tableView;
    private final ErrorLogWindow errorLogWindow;
    private final ConfigInfo configInfo;
    private final GitStatusDataFilterPane filterPane;
    private final ObjectProperty<TargetRepository> targetRepository = new SimpleObjectProperty<>(TargetRepository.SELECTED);
    private Menu menu;

    public ConvertCharSetPane2(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.errorLogWindow = new ErrorLogWindow(configInfo);
        configInfo.setBoolean("convertcharsetpane" + ".filter.enabled", true);
        this.filterPane = new GitStatusDataFilterPane(configInfo, "convertcharsetpane");
    }

    @Override
    public String getTitle() {
        return "CharSet";
    }

    @Override
    public Parent build() {
        tableView = new TableView<>();

        var fileTableColumn = new TableColumn<GitStatusData, String>("File");
        fileTableColumn.setPrefWidth(300);
        fileTableColumn.setCellValueFactory(p -> p.getValue().fileNameProperty());

        var encodingColumn = new TableColumn<GitStatusData, String>("Encoding");
        encodingColumn.setPrefWidth(300);
        encodingColumn.setCellValueFactory(p -> p.getValue().encodingProperty());
        
        tableView.getColumns().addAll(fileTableColumn, encodingColumn);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setBottom(filterPane.build());
        return borderPane;
    }

    @Override
    public void close() {
        //errorLogWindow.close();
        //informationLogWindow.close();
    }

    @Override
    public void saveConfig() {
        List<Double> widths = tableView.getColumns().stream().map(e -> e.getWidth()).collect(Collectors.toList());
        configInfo.setTableColumnWidth(tableView.getId(), widths);

        filterPane.saveConfig();
    }

    @Override
    public void setActive(boolean active) {
        menu.setVisible(active);
        
        this.active = active;
        if (!active) {
            return;
        }

        //System.out.println("ConvertCharSetPane2.setActive(): repositories=" + repositories.toString());
        long numNullRepository = repositories.getDatas().stream().filter(p -> p.getGitStatusDatas() == null).count();
        //LogWriter.writeLong("ConvertCharSetPane2.setActive()", "numNullRepository", numNullRepository);
        if (repositories.getDatas().size() == 0 || numNullRepository > 0) {
            refreshAll();
            return;
        }

        boolean isExistNoEncoding = false;
        for (var data : repositories.getDatas()) {
            if (data.getGitStatusDatas().stream().filter(p -> p.encodingProperty().get().equals("")).count() > 0) {
                isExistNoEncoding = true;
            }
        }
        if (isExistNoEncoding) {
            refreshAll();
        }
    }

    @Override
    public void setRepositories(RepositoriesInfo work) {
        if (this.repositories != null) {
            throw new RuntimeException("setRepositories() を実行するのは一回だけです。");
        }

        this.repositories = work;

        ListChangeListener<RepositoryData> selectedListener = (change) -> changeTargetRepositories(TargetRepository.SELECTED);
        work.getSelected().addListener(selectedListener);
        ListChangeListener<RepositoryData> changedListener = (change) -> changeTargetRepositories(TargetRepository.CHECKED);
        work.getChecked().addListener(changedListener);

        changeTargetRepositories(targetRepository.get());
    }

    private void changeTargetRepositories(TargetRepository target) {
        if (target != targetRepository.get()) {
            return;
        }

        ObservableList<RepositoryData> targetRepositories = getTargetRepositories();
        AggregatedObservableArrayList aggregated = new AggregatedObservableArrayList();
        if(targetRepositories.size() > 0)
            LogWriter.writeObject("ConvertCharSetPane2.changeTargetRepositories()", "e.getGitStatusDatas()", targetRepositories.get(0).getGitStatusDatas());
        targetRepositories.forEach(e -> aggregated.appendList(e.getGitStatusDatas()));
        Predicate<GitStatusData> p = pp -> {
            switch(pp.getIndexStatus()) {
                case "?":
                case "D":
                case "R":
                    return false;
            }
            if(pp.getWorkTreeStatus().equals("D")) {
                return false;
            }
            return true;
        };
        var indexFilteredList = new FilteredList<GitStatusData>(aggregated.getAggregatedList(), p);
        //var filteredList = new FilteredList<GitStatusData>(aggregated.getAggregatedList());
        var filteredList = new FilteredList<GitStatusData>(indexFilteredList);
        var sortableData = new SortedList<GitStatusData>(filteredList);
        LogWriter.writeObject("ConvertCharSetPane2.changeTargetRepositories()", "sortableData", sortableData);
        tableView.setItems(sortableData);
        sortableData.comparatorProperty().bind(tableView.comparatorProperty());
        filterPane.setFilteredList(filteredList);
        updateSituationSelectors();
    }

    private void updateSituationSelectors() {

    }

    private ObservableList<RepositoryData> getTargetRepositories() {
        return repositories.getTarget(targetRepository.get());
    }

    @Override
    public void setUp() {
        //updateSituationSelectors();
    }

    @Override
    public Menu buildMenu() {
        menu = new Menu("CharSet");
        menu.setId("convertCharSetMenu");
        menu.setVisible(false);
        return menu;
    }

    @Override
    public Parent buildToolBar() {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        return hbox;
    }

    @Override
    public ObjectProperty<TargetRepository> targetRepositoryProperty() {
        return targetRepository;
    }

    @Override
    public void refreshAll() {
        for (var repositoryData : repositories.getSelected()) {
            for (var statusData : repositoryData.getGitStatusDatas()) {
                boolean isCheckCharset = false;
                switch(statusData.getWorkTreeStatus()) {
                    case "M":
                        isCheckCharset = true;
                        break;
                }
                switch(statusData.getIndexStatus()) {
                    case "A":
                    case "M":
                        isCheckCharset = true;
                        break;                        
                }
                if (isCheckCharset) {
                    Path path = Paths.get(statusData.getRepositoryData().getPath().toString(), statusData.getFileName());
                    String charset = detectCharset(path.toString());
                    statusData.setEncoding(charset);
                }
            }
        }
    }

    @Override
    public void refreshChecked() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refreshSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String detectCharset(String fileName) {
        try (FileInputStream file = new FileInputStream(fileName); BufferedInputStream input = new BufferedInputStream(file); BOMInputStream bomIn = new BOMInputStream(input)) {
            boolean hasBom = bomIn.hasBOM();

            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            byte[] buf = new byte[1024];
            while ((nread = bomIn.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                if (hasBom) {
                    return encoding + " with BOM";
                } else {
                    return encoding;
                }
            } else {
                return "ASCII test";
            }
        } catch (FileNotFoundException ex) {
            errorLogWindow.appendException(ex);
            return "Fiel not found.";
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);
            return "IO exception";
        }
    }
}
