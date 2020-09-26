/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.filebrowser;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jeisi
 */
public class FileBrowser {

    private enum Type {
        UNSUPPORT,
        DESKTOP,
        EXPLORER,
    }
    private Type type;
    private static FileBrowser instance;
    
    public static FileBrowser getInstance() {
        if(instance == null) {
            instance = new FileBrowser();
        }
        return instance;
    }
    
    public boolean isSupported() {
        return (type != Type.UNSUPPORT);
    }

    public void browseFileDirectory(Path path) {
        switch (type) {
            case DESKTOP:
                Desktop.getDesktop().browseFileDirectory(path.toFile());
                break;
            case EXPLORER:
                openExplorer(path);
                break;
            case UNSUPPORT:
                throw new UnsupportedOperationException();
            default:
                throw new AssertionError(type + "　に対応するcase文が定義されていません。");
        }
    }

    private FileBrowser() {
        setupType();
    }

    private void setupType() {
        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
            type = Type.DESKTOP;
            return;
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            type = Type.EXPLORER;
            return;
        }
        type = Type.UNSUPPORT;
    }

    private void openExplorer(Path path) {
        final String command = "explorer.exe /SELECT,\"" + path.toAbsolutePath().toString() + "\"";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
