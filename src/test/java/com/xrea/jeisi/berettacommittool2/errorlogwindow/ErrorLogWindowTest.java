/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.errorlogwindow;

import com.xrea.jeisi.berettacommittool2.JTestUtility;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class ErrorLogWindowTest {

    private ErrorLogWindow errorLogWindow;

    public ErrorLogWindowTest() {
    }

    @Start
    public void start(Stage stage) {
        ConfigInfo configInfo = new ConfigInfo();
        errorLogWindow = new ErrorLogWindow(configInfo);
    }

    @Test
    public void testAppendText(FxRobot robot) throws InterruptedException {
        //System.out.println("ErrorLogWindowTest.testAppendText()");
        errorLogWindow.appendText("error0.\n");
        errorLogWindow.appendText("error1.\n");
        JTestUtility.waitForRunLater();

        TextArea textArea = robot.lookup("#ErrorLogWindowTextArea").queryAs(TextArea.class);
        int nRetryCounter = 0;
        while (!textArea.getText().equals("error0.\nerror1.\n") && ++nRetryCounter < 10) {
            Thread.sleep(100);
        }

        assertEquals("error0.\nerror1.\n", textArea.getText());

        //while (errorLogWindow.isShowing());
        Platform.runLater(() -> errorLogWindow.close());
    }

    @Test
    // GitCommandException の場合は StackTrace は表示しない。
    public void testAppendError(FxRobot robot) throws InterruptedException {
        JTestUtility.waitForRunLater();

        StackTraceElement[] element = new StackTraceElement[]{
            new StackTraceElement("org.eclipse.jgit.lib.BaseRepositoryBuilder", "build", "BaseRepositoryBuilder.java", 585),
            new StackTraceElement("org.eclipse.jgit.api.Git", "open", "Git.java", 91),
            new StackTraceElement("org.eclipse.jgit.api.Git", "open", "Git.java", 71),};
        Throwable throwable = new Throwable("");
        throwable.setStackTrace(element);
        String header = "command error:\n"
                + "[/home/jeisi/NetBeansProjects/.]\n"
                + "$ git status -s --untracked-files=all\n";
        GitCommandException ge = new GitCommandException(header, Arrays.asList(), Arrays.asList("fatal: not a git repository (or any of the parent directories): .git"));
        RepositoryNotFoundException exception = new RepositoryNotFoundException(Paths.get("/home/jeisi/test/git/sandbox/work/beretta/beretta"), ge);
        exception.setStackTrace(element);

        try {
            throw exception;
        } catch (RepositoryNotFoundException e) {
            //e.printStackTrace();
            errorLogWindow.appendException(e);
        }
        JTestUtility.waitForRunLater();

        TextArea textArea = robot.lookup("#ErrorLogWindowTextArea").queryAs(TextArea.class);
        int nRetryCounter = 0;
        while (!textArea.getText().split("\n")[0].equals("command error:") && nRetryCounter++ < 10) {
            Thread.sleep(1000);
        }

//        String expected = "com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException: repository not found: /home/jeisi/test/git/sandbox/work/beretta/beretta\n"
//                + "	at org.eclipse.jgit.lib.BaseRepositoryBuilder.build(BaseRepositoryBuilder.java:585)\n"
//                + "	at org.eclipse.jgit.api.Git.open(Git.java:91)\n"
//                + "	at org.eclipse.jgit.api.Git.open(Git.java:71)\n";
        String expected = "command error:\n"
                + "[/home/jeisi/NetBeansProjects/.]\n"
                + "$ git status -s --untracked-files=all\n"
                + "fatal: not a git repository (or any of the parent directories): .git\n";
        String actual = textArea.getText();
        assertEquals(expected, actual);
        //while (errorLogWindow.isShowing());
    }
}
