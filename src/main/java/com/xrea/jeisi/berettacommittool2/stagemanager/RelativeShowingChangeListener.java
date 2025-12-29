package com.xrea.jeisi.berettacommittool2.stagemanager;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.configinfo.StageEx;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public class RelativeShowingChangeListener implements ShowingChangeListener{
    private final ConfigInfo configInfo;
    private final StageEx stage;

    RelativeShowingChangeListener(ConfigInfo configInfo, StageEx stage) {
        this.configInfo = configInfo;
        this.stage = stage;
    }

    @Override
    public ChangeListener<Boolean> getChangeListener() {
        return (observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                var mainApp = configInfo.getMainApp();
                if (mainApp != null) {
                    Stage app = mainApp.getStage();
                    //configInfo.setRelativeWindowRectangle(identifier, stage.getX() - app.getX(), stage.getY() - app.getY(), scene.getWidth(), scene.getHeight());
                    configInfo.setRelativeWindowRectangle(stage.getIdentifier(), stage.getX() - app.getX(), stage.getY() - app.getY(), stage.getWidth(), stage.getHeight());
                    stage.onClose();
                }
            }
        };
    }
}
