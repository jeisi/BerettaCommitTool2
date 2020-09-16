/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 *
 * @author jeisi
 */
public class SwitchPane extends StackPane {

    private ObjectProperty<Node> constraints = new SimpleObjectProperty<>();

    public ObjectProperty<Node> constraintsProperty() {
        return constraints;
    }

    public void setConstraints(Node value) {
        constraintsProperty().set(value);
    }

    public void add(Node child) {
        getChildren().add(child);
        child.visibleProperty().bind(constraintsProperty().isEqualTo(child));
    }
}
