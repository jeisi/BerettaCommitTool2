/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.filterpane;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class RegexpPredicate implements Predicate<GitStatusData> {

    private final boolean caseInsensitive;
    private final String text;

    public RegexpPredicate(String text, boolean caseInsensitive) {
        this.text = text;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean test(GitStatusData t) {
        int flag = 0;
        if (caseInsensitive) {
            flag = Pattern.CASE_INSENSITIVE;
        }
        Pattern p = Pattern.compile(text, flag);
        String fileName = t.getFileName();
        Matcher m = p.matcher(fileName);
        boolean result = m.find();
        return result;
    }

}
