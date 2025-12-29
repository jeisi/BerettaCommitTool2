package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public class WindowStageManager extends AbsoluteStageManager {
    @Override
    public void open(StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo) {
        var listener = getShowingChangeListener(stage, windowRectangle, configInfo);
        doShowStage(stage, windowRectangle, configInfo, Stage::show, listener);
    }
}
