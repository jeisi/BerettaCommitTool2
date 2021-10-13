/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.xmlwriter;

/**
 *
 * @author jeisi
 */
public class LogWriter {
    public static void writeLong(String title, String name, long value) {
        System.out.println(String.format("%s: %s=%s", title, name, Long.toString(value)));
    }

    public static void writeObject(String title, String name, Object value) {
        String valueText = (value == null) ? "null" : value.toString();
        System.out.println(String.format("%s: %s=%s", title, name, valueText));
    }
    
    public static void writeMessage(String title, String message) {
        System.out.println(String.format("%s: %s", title, message));
    }
}
