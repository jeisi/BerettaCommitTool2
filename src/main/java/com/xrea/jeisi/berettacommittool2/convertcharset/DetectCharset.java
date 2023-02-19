/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author jeisi
 */
public class DetectCharset {

    public String detect(String fileName) throws IOException {
        try (FileInputStream file = new FileInputStream(fileName); BufferedInputStream input = new BufferedInputStream(file); BOMInputStream bomIn = new BOMInputStream(input)) {
            boolean hasBom = bomIn.hasBOM();

            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            byte[] buf = new byte[1024];
            while ((nread = bomIn.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                if (hasBom) {
                    return encoding + " with BOM";
                } else {
                    return encoding;
                }
            } else {
                return "ASCII text";
            }
        }
    }
}
