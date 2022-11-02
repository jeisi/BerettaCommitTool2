/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import java.io.IOException;
import javafx.application.Platform;

/**
 *
 * @author jeisi
 */
public class RepositoriesUtility {

    public static void setErrorName(RepositoryData repository, Exception e, ErrorLogWindow errorLogWindow) {
        try {
            throw e;
        } catch (RepositoryNotFoundException ex) {
            Platform.runLater(() -> showError(ex, errorLogWindow));
            repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getShortMessage()));
        } catch (IOException | GitConfigException | InterruptedException ex) {
            Platform.runLater(() -> showError(ex, errorLogWindow));
            repository.displayNameProperty().set(String.format("%s [error! %s]", repository.nameProperty().get(), ex.getMessage()));
        } catch (Exception ex) {
            repository.displayNameProperty().set(String.format("%s [unknown error! %s]", repository.nameProperty().get(), ex.getMessage()));
        }
    }

    private static void showError(Exception e, ErrorLogWindow errorLogWindow) {
        errorLogWindow.appendException(e);
    }
}
