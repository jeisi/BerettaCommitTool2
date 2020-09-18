/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jeisi
 */
public class SetUpWizard extends Stage {

    private final ConfigInfo configInfo;
    private final List<SetUpNode> nodes = new ArrayList<>();
    private int currentPage;
    private final List<String> programs;
    private SwitchPane switchPane;
    private Button nextButton;
    private Button backButton;

    public SetUpWizard(ConfigInfo configInfo, List<String> programs) {
        this.configInfo = configInfo;
        
        this.programs = programs.stream().filter(p -> configInfo.getProgram(p) == null).collect(Collectors.toList());
    }

    public List<String> getNullPrograms() {
        return programs;
    }

    public void exec() {
        for (var program : programs) {
            nodes.add(buildSetUpWinMerge(program));
        }

        Scene scene = new Scene(build());
        Stage stage = this;
        stage.setScene(scene);
        stage.setTitle("Set up programs wizard");
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                saveConfig();
            }
        });

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private Parent build() {
        ButtonBar buttonBar = new ButtonBar();
        backButton = new Button("Back");
        backButton.setOnAction(eh -> prevPage());
        backButton.setId("SetUpWizardBackButton");
        ButtonBar.setButtonData(backButton, ButtonData.BACK_PREVIOUS);
        nextButton = new Button("Next");
        nextButton.setOnAction(eh -> nextPage());
        nextButton.setId("SetUpWizardNextButton");
        ButtonBar.setButtonData(nextButton, ButtonData.NEXT_FORWARD);
        buttonBar.getButtons().addAll(backButton, nextButton);
        enableButtons();

        switchPane = new SwitchPane();
        nodes.forEach(e -> switchPane.add(e));
        switchPane.setConstraints(nodes.get(0));
        VBox vbox = new VBox(5, switchPane, buttonBar);

        /*        
        Pagination pagination = new Pagination(programs.size(), 0);
        pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
        VBox vbox = new VBox(5, pagination, buttonBar);
         */
        vbox.setPadding(new Insets(5, 5, 5, 5));
        return vbox;
    }

    private SetUpNode buildSetUpWinMerge(String program) {
        return new SetUpNode(program, this);
    }

    private void saveConfig() {
        nodes.forEach(node -> {
            String program = node.getProgram();
            String path = node.getPath();
            configInfo.setProgram(program, path);
        });
    }

    private void nextPage() {
        if (currentPage < nodes.size() - 1) {
            ++currentPage;
            switchPane.setConstraints(nodes.get(currentPage));
            enableButtons();
        } else {
            close();
        }
    }

    private void prevPage() {
        --currentPage;
        switchPane.setConstraints(nodes.get(currentPage));
        enableButtons();
    }

    private void enableButtons() {
        if (currentPage == 0) {
            backButton.setDisable(true);
        } else {
            backButton.setDisable(false);
        }
        if (currentPage == nodes.size() - 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
    }
}
