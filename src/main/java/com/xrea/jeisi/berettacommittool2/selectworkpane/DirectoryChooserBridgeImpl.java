/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.selectworkpane;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 *
 * @author jeisi
 */
public class DirectoryChooserBridgeImpl implements DirectoryChooserBridge {
    private DirectoryChooser chooser = new DirectoryChooser();
    
    @Override
    public File showDialog(Window ownerWindow) {
        return chooser.showDialog(ownerWindow);
    }
}
