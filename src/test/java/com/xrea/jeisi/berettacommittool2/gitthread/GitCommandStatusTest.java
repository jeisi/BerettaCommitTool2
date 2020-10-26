/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.RepositoryNotFoundException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class GitCommandStatusTest {

    private ConfigInfo configInfo;

    public GitCommandStatusTest() {
    }

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    // 出力結果が一行の時
    public void testStatus_OneLine() throws IOException, GitCommandException, GitConfigException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAddDeleted.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitStatusCommand gitCommand = new GitStatusCommand(workDir, configInfo);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        List<GitStatusData> list = gitCommand.status(repositoryData);
        // ファイル名でソートされている。
        assertThat("[{, D, a.txt, }]").isEqualTo(list.toString());
    }

    @Test
    public void testStatus_Lines() throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitStatusCommand gitCommand = new GitStatusCommand(workDir, configInfo);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        List<GitStatusData> list = gitCommand.status(repositoryData);
        // ファイル名でソートされている。
        assertThat("[{?, ?, a.txt, }, {?, ?, b.txt, }, {?, ?, c.txt, }]").isEqualTo(list.toString());
    }

    @Test
    public void testStatus_SpecifiedFile() throws IOException, InterruptedException, GitCommandException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitStatusCommand gitCommand = new GitStatusCommand(workDir, configInfo);

        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        List<GitStatusData> list = gitCommand.status(repositoryData, new GitStatusData("?", "?", "b.txt", repositoryData));
        // ファイル名でソートされている。
        assertThat("[{?, ?, b.txt, }]").isEqualTo(list.toString());
    }

    @Test
    // ディレクトリが git 作業ディレクトリでない場合は RepositoryNotFoundException がスローされる。
    public void testStatus_RepositoryNotFound() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.home");

        Path workDir = Paths.get(userDir);
        GitStatusCommand gitCommand = new GitStatusCommand(workDir, configInfo);
        RepositoryData repositoryData = new RepositoryData(true, "", Paths.get("."));
        RepositoryNotFoundException e = assertThrows(RepositoryNotFoundException.class, () -> gitCommand.status(repositoryData));
        assertEquals("repository not found: .", e.getMessage());
    }
}
