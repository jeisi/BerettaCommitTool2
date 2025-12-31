package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.stage.Stage;

public class WindowStageManager extends AbsoluteStageManager {
    public WindowStageManager(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        super(configInfo, identifier, defaultWidth, defaultHeight);
    }

    @Override
    public void open(Stage stage) {
        var listener = getShowingChangeListener(stage, windowRectangle, configInfo);
        doShowStage(stage, Stage::show, listener);
    }
}
