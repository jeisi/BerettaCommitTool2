package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public abstract class AbsoluteStageManager extends StageManager {
    public AbsoluteStageManager(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        super(configInfo, identifier, defaultWidth, defaultHeight);
    }

    @Override
    protected WindowRectangle getWindowRectangle(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        setUp();

        var windowRectangle = configInfo != null ? configInfo.getWindowRectangle(identifier) : null;
        double x, y, width, height;

        if (windowRectangle != null) {
            x = windowRectangle.getX();
            y = windowRectangle.getY();
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            return super.ClampWindowRectangle(x, y, width, height);
        } else {
            return new WindowRectangle(defaultWidth, defaultHeight);
        }
    }

    protected ChangeListener<Boolean> getShowingChangeListener(Stage stage, WindowRectangle windowRectangle, ConfigInfo configInfo) {
        return new AbsoluteShowingChangeListener(this).getChangeListener();
    }
}
