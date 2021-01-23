/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class CommitTab extends Tab implements BaseTab {

    private final ConfigInfo configInfo;
    private final Window parent;
    private CheckBox removeCommentCheckBox;

    public static String getTitle() {
        return "Commit";
    }

    public CommitTab(Window parent, ConfigInfo configInfo) {
        super(getTitle());
        this.configInfo = configInfo;
        this.parent = parent;
        setClosable(false);
        setContent(build());
    }

    @Override
    public void apply() {
        configInfo.setCommitMessageRemoveComment(removeCommentCheckBox.isSelected());
    }

    private Node build() {
        removeCommentCheckBox = new CheckBox("マージ時のデフォルトコミットメッセージのコメントアウトを外す。");
        removeCommentCheckBox.setSelected(configInfo.isCommitMessageRemoveComment());

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        vbox.getChildren().add(removeCommentCheckBox);
        return vbox;
    }
}
