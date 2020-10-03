/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class PreferencePane {

    private final ConfigInfo configInfo;
    private final Window parent;
    private final List<BaseTab> tabs = new ArrayList<>();
    private final List<EventHandler<ActionEvent>> actionEvents = new ArrayList<>();

    public PreferencePane(Window parent, ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.parent = parent;
    }

    public void close() {

    }

    public void addEventHandler(EventHandler<ActionEvent> actionEvent) {
        actionEvents.add(actionEvent);
    }

    public Parent build() {
        TabPane tabPane = new TabPane();
        ProgramsTab programTab = new ProgramsTab(parent, configInfo);
        Tab gitDiffTab = new Tab("git diff");
        gitDiffTab.setClosable(false);
        FontTab fontTab = new FontTab(parent, configInfo);
        tabPane.getTabs().addAll(fontTab, programTab, gitDiffTab);

        tabs.add(programTab);
        tabs.add(fontTab);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tabPane);
        borderPane.setBottom(buildBottom());
        return borderPane;
    }

    private Node buildBottom() {
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(eh -> cancel());
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(eh -> apply());
        ButtonBar.setButtonData(applyButton, ButtonBar.ButtonData.APPLY);

        Button okButton = new Button("OK");
        okButton.setOnAction(eh -> ok());
        ButtonBar.setButtonData(applyButton, ButtonBar.ButtonData.OK_DONE);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(cancelButton, applyButton, okButton);

        BorderPane.setMargin(buttonBar, new Insets(5, 5, 5, 5));
        return buttonBar;
    }

    private void cancel() {
        fireActionEvents(); // ウィンドウを閉じる
    }

    private void apply() {
        tabs.forEach(e -> e.apply());
    }

    private void ok() {
        apply();
        fireActionEvents(); // ウィンドウを閉じる
    }

    private void fireActionEvents() {
        ActionEvent e = new ActionEvent();
        for (var event : actionEvents) {
            event.handle(e);
        }
    }
}