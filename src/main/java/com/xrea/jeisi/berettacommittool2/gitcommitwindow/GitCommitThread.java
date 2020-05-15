/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.gitthread.GitCommitCommand;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;

/**
 *
 * @author jeisi
 */
public class GitCommitThread implements Runnable {

    private String message;
    private boolean amend;
    private GitCommitCommand commitCommand;

    public GitCommitThread(String message, boolean amend, GitCommitCommand commitCommand) {
        this.message = message;
        this.amend = amend;
        this.commitCommand = commitCommand;
    }

    @Override
    public void run() {
        try {
            commitCommand.commit(message, amend);
        } catch (NoMessageException ex) {
            throw new AssertionError("コミットメッセージが空文字ならば、GitCommitCommand.commit() が実行される前に弾かれていなければならない。");
        } catch (IOException ex) {
            Logger.getLogger(GitCommitThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GitAPIException ex) {
            Logger.getLogger(GitCommitThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
