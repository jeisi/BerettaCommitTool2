/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import java.util.List;
import javafx.scene.control.Dialog;

/**
 *
 * @author jeisi
 */
public class ConvertCharSetDialog extends Dialog {

    public ConvertCharSetDialog(List<String> files) {
        ConvertCharSetPane pane = new ConvertCharSetPane(files);
        
        setTitle("文字コード変換");
        setDialogPane(pane);
    }
    
}
