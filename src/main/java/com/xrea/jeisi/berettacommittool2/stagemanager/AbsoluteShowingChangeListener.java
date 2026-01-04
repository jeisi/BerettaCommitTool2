package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AbsoluteShowingChangeListener implements ShowingChangeListener {
    private final StageManager stageManager;

    public AbsoluteShowingChangeListener(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public ChangeListener<Boolean> getChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                Stage stage = stageManager.getStage();
                ConfigInfo configInfo = stageManager.getConfigInfo();
                Scene scene = stage.getScene();
                configInfo.setWindowRectangle(stageManager.getIdentifier(), stage.getX(), stage.getY(), scene.getWidth(), scene.getHeight());
                //configInfo.setWindowRectangle(stageManager.getIdentifier(), stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                //stage = null;
                stageManager.onClosed();
            }
        };
    }
}
