package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import javafx.beans.value.ChangeListener;

public abstract class AbsoluteStageManager extends StageManager {
    @Override
    public WindowRectangle getWindowRectangle(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        setUp();

        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(identifier) : null;
        double x, y, width, height;

        if (windowRectangle != null) {
            x = windowRectangle.getX();
            y = windowRectangle.getY();
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            return super.getWindowRectangle(x, y, width, height);
        } else {
            return new WindowRectangle(defaultWidth, defaultHeight);
        }
    }

    protected ChangeListener<Boolean> getShowingChangeListener(StageEx stage, WindowRectangle windowRectangle, ConfigInfo configInfo) {
        return new AbsoluteShowingChangeListener(configInfo, stage).getChangeListener();
    }
}
