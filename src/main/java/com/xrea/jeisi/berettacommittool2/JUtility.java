/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2;

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
}
