/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommitCommand;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author jeisi
 */
public class GitCommitPane {

    private ConfigInfo configInfo;
    private ErrorLogWindow errorLogWindow = new ErrorLogWindow();
    private TextArea messageTextArea;
    private ComboBox<String> summaryComboBox;
    private List<String> commitMessageHistory;
    private List<RepositoryData> repositoryDatas;
    private CheckBox amendCheckBox;
    private String amendMessage;

    public void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        this.commitMessageHistory = Collections.unmodifiableList(configInfo.getCommitMessageHistory());
    }

    public void setRepositoryDatas(List<RepositoryData> repositoryDatas) {
        this.repositoryDatas = repositoryDatas;

        amendMessage = getAmendMessage();
        if (amendMessage != null) {
            amendCheckBox.setDisable(false);
        }
    }

    public void close() {
        errorLogWindow.close();
    }

    public Parent build() {
        summaryComboBox = new ComboBox<>(getCommitMessages());
        summaryComboBox.setId("GitCommitPaneSummaryComboBox");
        summaryComboBox.setPrefWidth(400);
        summaryComboBox.setOnAction(e -> {
            int index = summaryComboBox.getSelectionModel().getSelectedIndex();
            messageTextArea.setText(commitMessageHistory.get(index));
        });

        amendCheckBox = new CheckBox("amend");
        amendCheckBox.setId("GitCommitPaneAmendCheckBox");
        amendCheckBox.setDisable(true);
        amendCheckBox.setOnAction(e -> amend());
        BorderPane.setAlignment(amendCheckBox, Pos.CENTER_RIGHT);

        BorderPane northPane = new BorderPane();
        northPane.setLeft(summaryComboBox);
        northPane.setRight(amendCheckBox);

        messageTextArea = new TextArea();
        messageTextArea.setId("GitCommitPaneMessageTextArea");
        BorderPane.setMargin(messageTextArea, new Insets(5, 0, 5, 0));

        Button commitButton = new Button("Commit");
        BorderPane.setAlignment(commitButton, Pos.CENTER);
        commitButton.setOnAction(e -> commit());

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(northPane);
        borderPane.setCenter(messageTextArea);
        borderPane.setBottom(commitButton);
        borderPane.setPadding(new Insets(5));
        //borderPane.setSpacing(5);
        return borderPane;
    }

    private void commit() {
        List<String> newCommitMessageHistory = new ArrayList<>();
        newCommitMessageHistory.add(messageTextArea.getText());
    }

    private void amend() {
        messageTextArea.setText(amendMessage);
    }

    private String getAmendMessage() {
        List<String> amendMessages = new ArrayList<>();
        for (var repositoryData : repositoryDatas) {
            GitCommitCommand commitCommand = new GitCommitCommand(repositoryData.getPath().toFile());
            try {
                String amendMessage = commitCommand.readCommitEditMsg();
                if (amendMessage == null) {
                    return null;
                }
                amendMessages.add(amendMessage);
            } catch (IOException ex) {
                errorLogWindow.appendException(ex);
            }
        }

        return String.join("---\n", amendMessages);
        //return amendMessages.stream().collect(Collectors.joining("\n---\n"));
    }

    private ObservableList<String> getCommitMessages() {
        if (configInfo == null) {
            return FXCollections.observableArrayList();
        }
        List<String> lists = configInfo.getCommitMessageHistory().stream().map(message -> getSummary(message)).collect(Collectors.toList());
        return FXCollections.observableArrayList(lists);
    }

    private String getSummary(String text) {
        final int SUMMARY_LENGTH = 40;
        if (text.length() < SUMMARY_LENGTH) {
            return text;
        } else {
            return text.substring(0, SUMMARY_LENGTH);
        }
    }
}
