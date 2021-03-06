/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.errorlogwindow.ErrorLogWindow;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNoMessageException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommitCommand;
import java.io.IOException;

/**
 *
 * @author jeisi
 */
public class GitCommitThread implements Runnable {

    private final String message;
    private final boolean amend;
    private final GitCommitCommand commitCommand;
    private final ErrorLogWindow errorLogWindow;

    public GitCommitThread(String message, boolean amend, GitCommitCommand commitCommand, ErrorLogWindow errorLogWindow) {
        this.message = message;
        this.amend = amend;
        this.commitCommand = commitCommand;
        this.errorLogWindow = errorLogWindow;
    }

    @Override
    public void run() {
        try {
            commitCommand.commit(message, amend);
        } catch (GitCommitNoMessageException ex) {
            throw new AssertionError("コミットメッセージが空文字ならば、GitCommitCommand.commit() が実行される前に弾かれていなければならない。");
        } catch (IOException ex) {
            errorLogWindow.appendException(ex);            
        } catch (GitConfigException ex) {
            errorLogWindow.appendException(ex);            
        } catch (InterruptedException ex) {
            errorLogWindow.appendException(ex);            
        }
    }

}
