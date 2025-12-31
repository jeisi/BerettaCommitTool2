package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.WindowRectangle;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

// 親クラスからの相対位置でウィンドウの座標を覚えておくタイプのウィンドウ
public class ChildWindowStageManager extends StageManager {
    public ChildWindowStageManager(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        super(configInfo, identifier, defaultWidth, defaultHeight);
    }

    @Override
    protected WindowRectangle getWindowRectangle(ConfigInfo configInfo, String identifier, double defaultWidth, double defaultHeight) {
        setUp();

        var windowRectangle = configInfo != null ? configInfo.getRelativeWindowRectangle(identifier) : null;
        double width, height;
        if (windowRectangle != null) {
            Stage app = configInfo.getMainApp().getStage();
            double x = windowRectangle.getX() + app.getX();
            double y = windowRectangle.getY() + app.getY();
            width = windowRectangle.getWidth();
            height = windowRectangle.getHeight();
            return super.ClampWindowRectangle(x, y, width, height);
        } else {
            return new WindowRectangle(defaultWidth, defaultHeight);
        }
    }

    @Override
    public void open(Stage stage) {
        var listener = getShowingChangeListener();
        doShowStage(stage, Stage::show, listener);
    }

    private ChangeListener<Boolean> getShowingChangeListener() {
        return new RelativeShowingChangeListener(this).getChangeListener();
    }
}
