/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.filebrowser;

import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
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
    private Type browseFileDirType;
    private Type openType;
    private ErrorLogWindow errorLogWindow;
    private static FileBrowser instance;

    public static FileBrowser getInstance() {
        if (instance == null) {
            instance = new FileBrowser();
        }
        return instance;
    }

    public FileBrowser setErrorLogWindow(ErrorLogWindow errorLogWindow) {
        this.errorLogWindow = errorLogWindow;
        return this;
    }

    public boolean isSupportedBrowseFileDir() {
        return (browseFileDirType != Type.UNSUPPORT);
    }

    public boolean isSupportedOpen() {
        return (openType != Type.UNSUPPORT);
    }

    public void browseFileDirectory(Path path) {
        switch (browseFileDirType) {
            case DESKTOP:
                Desktop.getDesktop().browseFileDirectory(path.toFile());
                break;
            case EXPLORER:
                openExplorerFile(path);
                break;
            case UNSUPPORT:
                throw new UnsupportedOperationException();
            default:
                throw new AssertionError(browseFileDirType + "　に対応するcase文が定義されていません。");
        }
    }

    public void browseDirectory(Path path) {
        switch (openType) {
            case DESKTOP:
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(path.toFile());
                    } catch (IOException ex) {
                        appendException(ex);
                    }
                }).start();
                break;
            case EXPLORER:
                openExplorerDir(path);
                break;
            case UNSUPPORT:
                throw new UnsupportedOperationException();
            default:
                throw new AssertionError(browseFileDirType + "　に対応するcase文が定義されていません。");
        }
    }

    private FileBrowser() {
        setupType();
    }

    private void setupType() {
        setupOpenType();
        setupBrowseFileDirType();
    }

    private void setupOpenType() {
        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            openType = Type.DESKTOP;
            return;
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            openType = Type.EXPLORER;
            return;
        }
        openType = Type.UNSUPPORT;
    }

    private void setupBrowseFileDirType() {
        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
            browseFileDirType = Type.DESKTOP;
            return;
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            browseFileDirType = Type.EXPLORER;
            return;
        }
        browseFileDirType = Type.UNSUPPORT;
    }

    private void openExplorerFile(Path path) {
        final String command = "explorer.exe /SELECT,\"" + path.toAbsolutePath().toString() + "\"";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            appendException(ex);
        }
    }

    private void openExplorerDir(Path path) {
        final String command = "explorer.exe \"" + path.toAbsolutePath().toString() + "\"";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            appendException(ex);
        }
    }

    private void appendException(Exception ex) {
        if (errorLogWindow != null) {
            errorLogWindow.appendException(ex);
        } else {
            ex.printStackTrace();
        }
    }
}
