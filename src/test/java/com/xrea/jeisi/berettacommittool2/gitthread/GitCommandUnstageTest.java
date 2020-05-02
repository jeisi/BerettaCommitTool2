/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class GitCommandUnstageTest {

    public GitCommandUnstageTest() {
    }

    @Test
    public void testUnstage() throws IOException, InterruptedException, GitAPIException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage2.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitUnstageCommand resetCommand = new GitUnstageCommand(workDir);
        resetCommand.unstage("b.txt");

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        GitStatusCommand statusCommand = new GitStatusCommand(workDir);
        List<GitStatusData> list = statusCommand.status(repositoryData);
        assertEquals("[{A, , c.txt, .}, {?, ?, b.txt, .}]", list.toString());
    }

    @Test
    // まだコミットがない状態では git reset HEAD <file> ではなく、git rm --cached <file> を実行する必要がある。
    public void testUnstageWhenNoCommit() throws IOException, InterruptedException, GitAPIException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitUnstageCommand resetCommand = new GitUnstageCommand(workDir);
        resetCommand.unstage("a.txt");

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        GitStatusCommand statusCommand = new GitStatusCommand(workDir);
        List<GitStatusData> list = statusCommand.status(repositoryData);
        assertEquals("[{A, , b.txt, .}, {?, ?, a.txt, .}]", list.toString());
    }
}
