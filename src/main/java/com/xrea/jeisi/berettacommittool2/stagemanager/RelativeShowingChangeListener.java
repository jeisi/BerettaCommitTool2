package com.xrea.jeisi.berettacommittool2.stagemanager;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public class RelativeShowingChangeListener implements ShowingChangeListener{
    private final StageManager stageManager;

    RelativeShowingChangeListener(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public ChangeListener<Boolean> getChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                var configInfo = stageManager.getConfigInfo();
                var stage = stageManager.getStage();
                var mainApp = configInfo.getMainApp();
                if (mainApp != null) {
                    Stage app = mainApp.getStage();
                    configInfo.setRelativeWindowRectangle(stageManager.getIdentifier(), stage.getX() - app.getX(), stage.getY() - app.getY(), stage.getWidth(), stage.getHeight());
                    stageManager.onClosed();
                }
            }
        };
    }
}
