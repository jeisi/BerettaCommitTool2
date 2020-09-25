/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactory;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommitCommand;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThread;
import com.xrea.jeisi.berettacommittool2.gitthread.GitThreadMan;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private final ErrorLogWindow errorLogWindow = new ErrorLogWindow();
    private TextArea messageTextArea;
    private ComboBox<String> summaryComboBox;
    private List<String> commitMessageHistory = new ArrayList<>();
    private List<RepositoryData> repositoryDatas = new ArrayList<>();
    private CheckBox amendCheckBox;
    private String amendMessage;
    private GitCommandFactory gitCommandFactory;
    private List<EventHandler<ActionEvent>> actionEvents = new ArrayList<>();
    private static int SUMMARY_LENGTH = 40;

    void setConfigInfo(ConfigInfo configInfo) {
        this.configInfo = configInfo;
        var commitMessageHistory = configInfo.getCommitMessageHistory();
        if (commitMessageHistory != null) {
            this.commitMessageHistory = Collections.unmodifiableList(commitMessageHistory);
        } else {
            this.commitMessageHistory = new ArrayList<>();
        }
    }

    public void setRepositoryDatas(List<RepositoryData> repositoryDatas) {
        this.repositoryDatas = repositoryDatas;

        amendMessage = getAmendMessage();
        if (amendMessage != null) {
            amendCheckBox.setDisable(false);
        }
    }

    public void setGitCommandFactory(GitCommandFactory gitCommandFactory) {
        this.gitCommandFactory = gitCommandFactory;
    }

    public void addEventHandler(EventHandler<ActionEvent> actionEvent) {
        actionEvents.add(actionEvent);
    }
    
    static void setSummaryLength(int length) {
        SUMMARY_LENGTH = length;
    }

    public void close() {
        saveConfig();
        errorLogWindow.close();
    }

    private void saveConfig() {
        if (configInfo == null) {
            return;
        }

        configInfo.setCommitMessageHistory(commitMessageHistory);
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
        commitButton.setId("GitCommitPaneCommitButton");
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
        String commitMessage = messageTextArea.getText();
        if (commitMessage.length() == 0) {
            String errorMessage = "正しいコミット・メッセージは:\n"
                    + " - 第１行: 何をしたか、を１行で要約。\n"
                    + " - 第２行: 空白\n"
                    + " - 残りの行: なぜ、この変更が良い変更か、の説明。";
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
            alert.setHeaderText("コミット・メッセージを入力してください");
            alert.showAndWait();
            return;
        }

        addCommitMessageToHistory();

        repositoryDatas.forEach((var repositoryData) -> {
            String workDir = repositoryData.getPath().toString();
            GitThread thread = GitThreadMan.get(workDir);
            GitCommitCommand commitCommand = gitCommandFactory.createGitCommitCommand(repositoryData.getPath().toFile());
            thread.addCommand(new GitCommitThread(commitMessage, amendCheckBox.isSelected(), commitCommand, errorLogWindow));
        });
        
        fireActionEvents();
    }

    private void fireActionEvents() {
        ActionEvent e = new ActionEvent();
        for(var event : actionEvents) {
            event.handle(e);
        }
    }
    
    private void addCommitMessageToHistory() {
        System.out.println("GitCommitPane.addCommitMessageToHistory()");
        var commitMessage = messageTextArea.getText();
        if (commitMessageHistory.size() > 0 && commitMessage.equals(commitMessageHistory.get(0))) {
            return;
        }

        List<String> newCommitMessageHistory = new ArrayList<>();
        newCommitMessageHistory.add(commitMessage);
        for (var message : commitMessageHistory) {
            if (!message.equals(commitMessage)) {
                newCommitMessageHistory.add(message);
            }
            if (newCommitMessageHistory.size() > 30) {
                break;
            }
        }
        commitMessageHistory = newCommitMessageHistory;
        summaryComboBox.getItems().setAll(getCommitMessages());
    }

    private void amend() {
        if (amendCheckBox.isSelected()) {
            messageTextArea.setText(amendMessage);
        } else {
            messageTextArea.setText("");
        }
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
        //List<String> lists = configInfo.getCommitMessageHistory().stream().map(message -> getSummary(message)).collect(Collectors.toList());
        List<String> lists = commitMessageHistory.stream().map(message -> getSummary(message)).collect(Collectors.toList());
        return FXCollections.observableArrayList(lists);
    }

    private String getSummary(String text) {
        if (text.length() < SUMMARY_LENGTH) {
            return text;
        } else {
            return text.substring(0, SUMMARY_LENGTH);
        }
    }
}
