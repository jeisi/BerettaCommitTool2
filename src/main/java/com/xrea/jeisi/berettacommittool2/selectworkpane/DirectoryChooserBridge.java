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
public interface DirectoryChooserBridge {
    public File showDialog(Window ownerWindow);
    public void setInitialDirectory(File value);
}
