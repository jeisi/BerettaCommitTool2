/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.progresswindow;

import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jeisi
 */
public class ProgressModel {

    private int currentValue;
    private final int maxValue;
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final String title;
    private final StringProperty countText = new SimpleStringProperty();
    private CompleteListener listener;

    public ProgressModel(String title, int maxValue) {
        this.title = title;
        this.maxValue = maxValue;
        setCurrentValue(0);
    }

    public final void setCurrentValue(int currentValue) {
        XmlWriter.writeStartMethod("ProgressModel.setCurrentValue(%d)", currentValue);
        
        this.currentValue = currentValue;
        Platform.runLater(() -> {
            countText.set(String.format("%3d/%3d", currentValue, maxValue));
            progress.set((double) currentValue / (double) maxValue);
            if (currentValue >= maxValue) {
                if (listener != null) {
                    listener.complete(this);
                }
            }
        });
        
        XmlWriter.writeEndMethod();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public StringProperty countTextProperty() {
        return countText;
    }

    public String getTitle() {
        return title;
    }

    public void setCompleteListener(CompleteListener listener) {
        this.listener = listener;
    }
}
