/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;

public class SituationVisible {

    private Situation situation;
    private final List<Button> items = new ArrayList<>();

    public SituationVisible() {
    }

    public void setSituation(Situation situation) {
        this.situation = situation;
    }

    public List<Button> getItems() {
        return items;
    }

    public void update() {
        //XmlWriter.writeStartMethod("SituationVisible.update()");

        boolean isValid = situation.isValid();
        items.forEach(item -> item.setVisible(isValid));
        items.forEach(item -> item.setManaged(isValid));

        //XmlWriter.writeEndMethod();
    }
}
