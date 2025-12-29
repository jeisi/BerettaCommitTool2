package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;

public class AbsoluteShowingChangeListener implements ShowingChangeListener {
    private final ConfigInfo configInfo;
    private final StageEx stage;

    public AbsoluteShowingChangeListener(ConfigInfo configInfo, StageEx stage) {
        this.configInfo = configInfo;
        this.stage = stage;
    }

    @Override
    public ChangeListener<Boolean> getChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                Scene scene = stage.getScene();
                configInfo.setWindowRectangle(stage.getIdentifier(), stage.getX(), stage.getY(), scene.getWidth(), scene.getHeight());
                //stage = null;
                stage.onClose();
            }
        };
    }
}
