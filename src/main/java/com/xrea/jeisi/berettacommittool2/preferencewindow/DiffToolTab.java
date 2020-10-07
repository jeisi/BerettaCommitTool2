/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.preferencewindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class DiffToolTab extends Tab implements BaseTab {

    private final ConfigInfo configInfo;
    private final Window parent;
    private ToggleGroup group;

    public static String getTitle() {
        return "DiffTool";
    }
    
    public DiffToolTab(Window parent, ConfigInfo configInfo) {
        super(getTitle());
        this.configInfo = configInfo;
        this.parent = parent;
        setClosable(false);
        setContent(build());
    }

    @Override
    public void apply() {
        configInfo.setDiffTool(((RadioButton) group.getSelectedToggle()).getText());
    }

    private Node build() {
        Label label = new Label("git difftool 時に使用する GUI ツール:");

        group = new ToggleGroup();
        RadioButton winmergeRB = new RadioButton("winmerge");
        winmergeRB.setToggleGroup(group);
        RadioButton meldRB = new RadioButton("meld");
        meldRB.setId("DiffToolTabMeldRadioButton");
        meldRB.setToggleGroup(group);
        RadioButton p4mergeRB = new RadioButton("p4merge");
        p4mergeRB.setToggleGroup(group);

        var difftool = configInfo.getDiffTool();
        var selectedLists = group.getToggles().filtered(e -> ((RadioButton) e).getText().equals(difftool));
        RadioButton selectedRB;
        if (selectedLists.size() > 0) {
            selectedRB = (RadioButton) selectedLists.get(0);
        } else {
            // 本来はここに来るべきではないが、万一の時のための対応。
            selectedRB = (RadioButton) group.getToggles().get(0);
        }
        selectedRB.setSelected(true);
        selectedRB.requestFocus();

        VBox vbox = new VBox(label, winmergeRB, meldRB, p4mergeRB);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5));
        return vbox;
    }

    private void checkTool() {
        
    }
}
