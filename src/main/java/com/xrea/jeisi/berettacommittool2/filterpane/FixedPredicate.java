/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;

/**
 *
 * @author jeisi
 */
public class FixedPredicate implements Predicate<GitStatusData> {

    private final boolean caseInsensitive;
    private final String text;

    public FixedPredicate(String text, boolean caseInsensitive) {
        this.text = text;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean test(GitStatusData t) {
        String fileName = t.getFileName();
        if (caseInsensitive) {
            // insensitive
            boolean result = (fileName.toLowerCase().contains(text.toLowerCase()));
            return result;
        } else {
            // sensitive
            boolean result = (fileName.contains(text));
            return result;
        }
    }

}
