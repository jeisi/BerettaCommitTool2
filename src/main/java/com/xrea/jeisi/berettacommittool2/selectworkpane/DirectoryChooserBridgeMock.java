/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import java.io.File;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class DirectoryChooserBridgeMock implements DirectoryChooserBridge {
    private File file;
    
    public DirectoryChooserBridgeMock(File file) {
        this.file = file;
    }
    
    @Override
    public File showDialog(Window ownerWindow) {
        return file;
    }
    
}
