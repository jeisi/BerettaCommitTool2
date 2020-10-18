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
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitCommandUnstageTest {

    private ConfigInfo configInfo;
    
    public GitCommandUnstageTest() {
    }

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    public void testUnstage() throws IOException, InterruptedException, GitAPIException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitUnstageCommand resetCommand = new GitUnstageCommand(workDir, configInfo);
        resetCommand.unstage(new GitStatusData("?", "?", "b.txt", repositoryData));

        GitStatusCommand statusCommand = new GitStatusCommand(workDir, configInfo);
        List<GitStatusData> list = statusCommand.status(repositoryData);
        assertEquals("[{A, , c.txt, .}, {?, ?, b.txt, .}]", list.toString());
    }

    @Test
    // まだコミットがない状態では git reset HEAD <file> ではなく、git rm --cached <file> を実行する必要がある。
    public void testUnstageWhenNoCommit() throws IOException, InterruptedException, GitAPIException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitUnstageCommand resetCommand = new GitUnstageCommand(workDir, configInfo);
        resetCommand.unstage(new GitStatusData("?", "?", "a.txt", repositoryData));

        GitStatusCommand statusCommand = new GitStatusCommand(workDir, configInfo);
        List<GitStatusData> list = statusCommand.status(repositoryData);
        assertEquals("[{A, , b.txt, .}, {?, ?, a.txt, .}]", list.toString());
    }
}
