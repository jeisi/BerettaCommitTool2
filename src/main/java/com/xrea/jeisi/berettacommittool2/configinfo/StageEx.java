package com.xrea.jeisi.berettacommittool2.configinfo;

import javafx.stage.Stage;

public class StageEx extends Stage {
    private final String identifier;

    public StageEx(String identifier) {
        super();
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void onClose() {}
}
