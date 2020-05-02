/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.streamcaputurer;

import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.control.TextArea;

/**
 *
 * @author jeisi
 */
public class StreamCapturer extends OutputStream {

    private final StringBuilder buffer;
    private final TextArea textArea;

    public StreamCapturer(TextArea textArea) {
        //System.out.println("StreamCapturer()");
        this.textArea = textArea;
        buffer = new StringBuilder(128);
    }

    @Override
    public void write(int b) throws IOException {
        //System.out.println(String.format("StreamCapture.write(%d)", b));
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            textArea.appendText(buffer.toString());
            buffer.delete(0, buffer.length());
        }
        //old.print(c);
    }

    @Override
    public void write(byte[] b) throws IOException {
        //System.out.println("StreamCapture.write(byte[] b)");
        textArea.appendText(new String(b));
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        //System.out.println("StreamCapture.write(byte[] b, int off, int len)");
        textArea.appendText(new String(b, off, len));
    }
}
