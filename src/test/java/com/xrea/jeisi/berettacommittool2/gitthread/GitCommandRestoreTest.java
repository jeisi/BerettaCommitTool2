/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author jeisi
 */
public class GitCommandRestoreTest {

    private ConfigInfo configInfo;

    public GitCommandRestoreTest() {
    }

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    // configInfo.versionInfo が設定されていない時は GitConfigException がスローされる。
    public void testWithoutVersionInfo() throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiffCached.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitRestoreCommand restoreCommand = new GitRestoreCommand(workDir, configInfo);
        List<GitStatusData> datas = new ArrayList<>();
        datas.add(new GitStatusData("M", "", "a.txt", repositoryData));
        assertThrows(GitConfigException.class, () -> restoreCommand.restoreReset(datas));
    }

    @Test
    // ver.2.23.0 以降では git restore コマンドが直接使用できる。
    public void testNative() throws IOException, InterruptedException, GitConfigException {
        configInfo.setVersionInfo(new VersionInfo(2, 23, 0));

        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiffCached.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitRestoreCommand restoreCommand = new GitRestoreCommand(workDir, configInfo);
        List<GitStatusData> datas = new ArrayList<>();
        datas.add(new GitStatusData("M", "", "a.txt", repositoryData));
        restoreCommand.restoreReset(datas);

        String expected = "bbb\n";
        String actual = Files.readString(Paths.get(workDir.toString(), "a.txt"));
        assertEquals(expected, actual);
    }

    @Test
    public void testCompatible() throws IOException, InterruptedException, GitConfigException {
        configInfo.setVersionInfo(new VersionInfo(2, 22, 0));

        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiffCached.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        RepositoryData repositoryData = new RepositoryData(true, ".", Paths.get("."));
        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitRestoreCommand restoreCommand = new GitRestoreCommand(workDir, configInfo);
        List<GitStatusData> datas = new ArrayList<>();
        datas.add(new GitStatusData("M", "", "a.txt", repositoryData));
        restoreCommand.restoreReset(datas);

        String expected = "bbb\n";
        String actual = Files.readString(Paths.get(workDir.toString(), "a.txt"));
        assertEquals(expected, actual);
    }
}
