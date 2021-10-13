/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

/**
 *
 * @author jeisi
 */
public class ConvertCharSetPane {

    private final ConfigInfo configInfo;
    private ErrorLogWindow errorLogWindow;
    private Label encodeLabel;

    public ConvertCharSetPane(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public void close() {
        if (errorLogWindow != null) {
            errorLogWindow.close();
        }
    }

    public Parent build() {

        Label label = new Label("Encode: ");

        encodeLabel = new Label("");

        HBox hbox = new HBox(label, encodeLabel);

        Button button = new Button("UTF-8 with BOM に変換");
        button.setDisable(true);

        VBox vbox = new VBox(hbox, button);
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        return vbox;
    }

    public void setFile(String fileName) {
        try ( FileInputStream file = new FileInputStream(fileName);  BufferedInputStream input = new BufferedInputStream(file);  BOMInputStream bomIn = new BOMInputStream(input)) {
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
                if(hasBom) {
                    encodeLabel.setText(encoding + " with BOM");
                } else {
                    encodeLabel.setText(encoding);
                }
            } else {
                encodeLabel.setText("No encoding detected.");
            }

        } catch (FileNotFoundException ex) {
            appendException(ex);
        } catch (IOException ex) {
            appendException(ex);
        }
    }

    private void appendException(Exception e) {
        if (errorLogWindow == null) {
            errorLogWindow = new ErrorLogWindow(configInfo);
        }
        errorLogWindow.appendException(e);
    }
}
