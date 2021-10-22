/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class StringRegexpPredicate implements Predicate<String> {

    private final boolean caseInsensitive;
    private final String text;

    public StringRegexpPredicate(String text, boolean caseInsensitive) {
        this.text = text;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean test(String t) {
        int flag = 0;
        if (caseInsensitive) {
            flag = Pattern.CASE_INSENSITIVE;
        }
        Pattern p = Pattern.compile(text, flag);
        Matcher m = p.matcher(t);
        boolean result = m.find();
        return result;
    }
}
