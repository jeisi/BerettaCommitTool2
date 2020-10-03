/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandFactoryImpl;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitCommitWindowTestNoMessage {

    private GitCommitWindow app;

    public GitCommitWindowTestNoMessage() {
    }

    @Start
    public void start(Stage stage) {
        ConfigInfo configInfo = new ConfigInfo();
        app = new GitCommitWindow(configInfo);
        app.getGitCommitPane().setGitCommandFactory(new GitCommandFactoryImpl());
        app.open();
    }

    @Test
    public void testNoCommitMessage(FxRobot robot) throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();
        
        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get(userDir, "src/test/resources/work/beretta"));
        List<RepositoryData> datas = new ArrayList<RepositoryData>();
        datas.add(repositoryData);
        app.getGitCommitPane().setRepositoryDatas(datas);

        robot.clickOn("#GitCommitPaneCommitButton");
        
        //while(true);
    }
    
    @Disabled
    @Test
    // Git リポジトリが存在しないディレクトリを指定された時。
    public void testNoGitRepository() {
        
    }
}
