package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import javafx.stage.Stage;

import java.awt.*;

public class DialogStageManager extends AbsoluteStageManager {
    @Override
    public void open(StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo) {
        var listener = getShowingChangeListener(stage, windowRectangle, configInfo);
        doShowStage(stage, windowRectangle, configInfo, Stage::showAndWait, listener);
    }
}
