/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 *
 * @author jeisi
 */
public class ConvertCharSetPane extends DialogPane {

    private final ObservableList<String> files;

    public ConvertCharSetPane(List<String> files) {
        this.files = FXCollections.observableArrayList(files);

        setContent(build());
        getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
    }

    public Parent build() {
        Label confirmLabel = new Label("以下のファイルの文字コードを UTF-8 without BOM から UTF-8 with BOM に変換します。");

        ListView<String> filesListView = new ListView<>();
        filesListView.setPrefWidth(100);
        filesListView.setPrefHeight(300);
        filesListView.setItems(files);

        VBox vbox = new VBox(confirmLabel, filesListView);
        vbox.setSpacing(5);
        return vbox;
    }

    private void doConvert() {

    }
}
