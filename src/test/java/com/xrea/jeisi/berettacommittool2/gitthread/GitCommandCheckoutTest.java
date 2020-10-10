/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitCommandCheckoutTest {

    private ConfigInfo configInfo;
    private ProgressWindow progressWindow;

    public GitCommandCheckoutTest() {
    }

    @Start
    public void start(Stage stage) {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);

        progressWindow = new ProgressWindow(configInfo);
    }

    @Test
    public void testCheckoutHead() throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAddUpdate.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCheckoutCommand checkoutCommand = new GitCheckoutCommand(workDir, configInfo);
        checkoutCommand.checkoutHead("test01.cpp");

        GitStatusCommand statusCommand = new GitStatusCommand(workDir, configInfo);
        var files = statusCommand.status(new RepositoryData(true, ".", workDir));
        assertEquals(9, files.size());
    }

    @Test
    public void testCheckoutHeadFiles() throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAddUpdate.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCheckoutCommand checkoutCommand = new GitCheckoutCommand(workDir, configInfo);
        checkoutCommand.setProgressWindow(progressWindow);
        checkoutCommand.checkoutHead("test01.cpp", "test02.cpp", "test03.cpp", "test04.cpp", "test05.cpp",
                "test06.cpp", "test07.cpp", "test08.cpp", "test09.cpp", "test10.cpp");

        GitStatusCommand statusCommand = new GitStatusCommand(workDir, configInfo);
        var files = statusCommand.status(new RepositoryData(true, ".", workDir));
        assertEquals(0, files.size());
    }
}
