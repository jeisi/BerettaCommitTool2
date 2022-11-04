/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.TargetRepository;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.repositoriesinfo.RepositoriesInfo;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoriesPane;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.LogWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class CharSetPaneTest {

    private ConfigInfo configInfo;
    private CharSetPane app;
    private RepositoriesPane repositoriesPane;
    private Stage stage;

    public CharSetPaneTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        Platform.runLater(() -> {
            stage.close();
            GitThreadMan.closeAll();
        });
    }

    @Start
    public void start(Stage stage) {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);

        this.stage = stage;
        app = new CharSetPane(configInfo);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(app.buildMenu());

        repositoriesPane = new RepositoriesPane();

        HBox hbox = new HBox();
        hbox.getChildren().addAll(repositoriesPane.build(), app.build());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(menuBar, hbox);
        Scene scene = new Scene(vbox, 900, 480);
        stage.setScene(scene);
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                app.close();
            }
        });
        stage.show();

    }

    @Test
    public void test(FxRobot robot) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testConvertCharSet.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work");

        TableView<RepositoryData> repositoryTableView = robot.lookup("#tableView").queryAs(TableView.class);
        var work = new RepositoriesInfo(repositoryTableView);

        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");
        work.setRepositories(repositories, workDir.toString());

        repositoriesPane.setRepositories(work);
        app.setRepositories(work);

        // RepositoriesPane で beretta を選択
        Platform.runLater(() -> repositoryTableView.getSelectionModel().select(0));
        JTestUtility.waitForRunLater();
        app.refreshAll();
        JTestUtility.waitForRunLater();
        Platform.runLater(() -> app.setActive(true));
        JTestUtility.waitForRunLater();

        // RepositoriesPane で beretta が選択されている状態では表示は
        //     a.txt | UTF-8      | beretta
        //     b.txt | ASCII text | beretta
        // となる。
        TableView<RepositoryData> appTableView = robot.lookup("#ConvertCharSetPane2tableView").queryAs(TableView.class);
        String result = getDisplayCellDatas(appTableView);
        String expect = "{a.txt,UTF-8,beretta},{b.txt,ASCII text,beretta}";
        assertThat(result.equals(expect));

        // RepositoriesPane で beretta/gyp を選択
        Platform.runLater(() -> {
            repositoryTableView.getSelectionModel().clearSelection();
            repositoryTableView.getSelectionModel().select(1);
        });
        JTestUtility.waitForRunLater();

        // RepositoriesPane で beretta/gyp が選択されている状態では表示は
        //     d.txt | UTF-8 with BOM | beretta/gyp
        // となる。
        result = getDisplayCellDatas(appTableView);
        expect = "{d.txt,UTF-8 with BOM,beretta/gyp}";
        //assertThat(result.equals(expect));
        assertEquals(result, expect);

        // Checked を選択。
        app.targetRepositoryProperty().set(TargetRepository.CHECKED);

        // Checked が選択されているときは、（全行が対象となるので）表示は
        //     a.txt | UTF-8          | beretta
        //     b.txt | ASCII text     | beretta
        //     d.txt | UTF-8 with BOM | beretta/gyp
        // となる。
        result = getDisplayCellDatas(appTableView);
        expect = "{a.txt,UTF-8,beretta},{b.txt,ASCII text,beretta},{d.txt,UTF-8 with BOM,beretta/gyp}";
        //assertThat(result.equals(expect));
        assertEquals(result, expect);

        int nCounter = 0;
        while (stage.isShowing() && ++nCounter < 60) {
            Thread.sleep(1000);
        }
    }

    private String getDisplayCellDatas(TableView<RepositoryData> tableView) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        int nNumItems = tableView.getItems().size();
        for (int item = 0; item < nNumItems; ++item) {
            if (!isFirst) {
                builder.append(",");
            }
            isFirst = false;
            builder.append("{");

            boolean isFirstColumn = true;
            for (var column : tableView.getColumns()) {
                if (!isFirstColumn) {
                    builder.append(",");
                }
                isFirstColumn = false;
                builder.append(column.getCellData(item).toString());
            }

            builder.append("}");
        }
        return builder.toString();
    }
}
