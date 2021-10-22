/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import java.util.function.Predicate;

/**
 *
 * @author jeisi
 */
public class StringFixedPredicate implements Predicate<String> {

    private final boolean caseInsensitive;
    private final String text;

    public StringFixedPredicate(String text, boolean caseInsensitive) {
        this.text = text;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean test(String t) {
        if (caseInsensitive) {
            // insensitive
            boolean result = (t.toLowerCase().contains(text.toLowerCase()));
            return result;
        } else {
            // sensitive
            boolean result = (t.contains(text));
            return result;
        }
    }
}
