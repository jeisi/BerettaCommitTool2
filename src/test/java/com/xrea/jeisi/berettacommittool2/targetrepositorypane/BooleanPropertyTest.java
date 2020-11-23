/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.targetrepositorypane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class BooleanPropertyTest {

    private enum Type {
        ONE,
        TWO,
    };

    public BooleanPropertyTest() {
    }

    @Test
    public void testBind() {
        BooleanProperty b = new SimpleBooleanProperty();
        ObjectProperty<Type> t = new SimpleObjectProperty<>(Type.ONE);

        b.bind(t.isEqualTo(Type.ONE));
        assertEquals(true, b.get());

        t.set(Type.TWO);
        assertEquals(false, b.get());

        t.set(Type.ONE);
        assertEquals(true, b.get());
    }

    @Test
    public void testBindReattach() {
        BooleanProperty b = new SimpleBooleanProperty(true);
        ObjectProperty<Type> t = new SimpleObjectProperty<>(Type.ONE);
        ObjectProperty<Type> s = new SimpleObjectProperty<>(Type.TWO);

        b.bind(t.isEqualTo(Type.ONE));
        assertEquals(true, b.get());

        b.bind(s.isEqualTo(Type.ONE));
        assertEquals(false, b.get());

    }

}
