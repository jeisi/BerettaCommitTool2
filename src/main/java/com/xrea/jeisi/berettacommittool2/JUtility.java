/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class JUtility {

    public static void printStackTrace() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            System.out.println(ste[i]); // スタックトレースの情報を整形して表示
        }
    }

    public static Path expandPath(String currentDir, String dir, String... more) {
        final Pattern p = Pattern.compile("^[A-Za-z]:/.*");
        Matcher m = p.matcher(dir);
        if (m.matches()) {
            return Paths.get(dir, more);
        } else if (more.length == 0) {
            return Paths.get(currentDir, dir);
        } else {
            //return Paths.get(Paths.get(currentDir, new String[]{dir}).toString(), more);
            String[] concatenatedArray = new String[more.length + 1];
            concatenatedArray[0] = dir;
            System.arraycopy(more, 0, concatenatedArray, 1, more.length);
            return Paths.get(currentDir, concatenatedArray);
        }
    }
}
