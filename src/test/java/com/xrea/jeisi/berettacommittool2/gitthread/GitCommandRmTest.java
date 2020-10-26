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
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.progresswindow.ProgressWindow;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
public class GitCommandRmTest {

    private ConfigInfo configInfo;
    private ProgressWindow progressWindow;

    public GitCommandRmTest() {
    }

    @Start
    public void start(Stage stage) {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);

        progressWindow = new ProgressWindow(configInfo);
    }

    @Test
    public void testAdd() throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testRename.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitRmCommand rmCommand = new GitRmCommand(workDir, configInfo);
        rmCommand.rm(new GitStatusData("R", "", "pre-push -> pre-push.sh", repositoryData));

        GitStatusCommand statusCommand = new GitStatusCommand(workDir, configInfo);
        List<GitStatusData> list = statusCommand.status(repositoryData, new GitStatusData("D", "", "pre-push", repositoryData));
        assertEquals("[{D, , pre-push, .}]", list.toString());
    }
}
