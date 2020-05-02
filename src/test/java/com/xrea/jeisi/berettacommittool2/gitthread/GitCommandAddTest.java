/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javafx.stage.Stage;
import org.eclipse.jgit.api.errors.GitAPIException;
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
public class GitCommandAddTest {

    private ProgressWindow progressWindow;

    public GitCommandAddTest() {
    }

    @Start
    public void start(Stage stage) {
        progressWindow = new ProgressWindow();
    }

    @Test
    public void testAdd() throws IOException, InterruptedException, GitAPIException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitAddCommand addCommand = new GitAddCommand(workDir);
        addCommand.add("a.txt");

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        GitStatusCommand statusCommand = new GitStatusCommand(workDir);
        List<GitStatusData> list = statusCommand.status(repositoryData, "a.txt");
        assertEquals("[{A, , a.txt, .}]", list.toString());
    }

    @Test
    public void testAddWithProgressWindow() throws IOException, GitAPIException, InterruptedException {
        System.out.println("GitCommandAddTest.testAddWithProgressWindow()");
        File workDir = Paths.get("src/test/resources/work/beretta").toFile();
        MockGitAddCommand addCommand = new MockGitAddCommand(workDir);
        addCommand.setProgressWindow(progressWindow);
        addCommand.add("test01.cpp", "test02.cpp", "test03.cpp", "test04.cpp", "test05.cpp",
                "test06.cpp", "test07.cpp", "test08.cpp", "test09.cpp", "test10.cpp");
        Thread.sleep(1000);
    }
}
