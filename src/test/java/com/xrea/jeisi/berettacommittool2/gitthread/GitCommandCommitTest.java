/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author jeisi
 */
public class GitCommandCommitTest {

    private ConfigInfo configInfo;

    public GitCommandCommitTest() {
    }

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    public void testReadCommitEditMsg() throws IOException, InterruptedException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitMessage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
        assertEquals("テスト\nメッセージ", commitCommand.readCommitEditMsg());
    }

//    @Test
//    public void testCommit_ステージにファイルが登録されていなければコミットは実行されない() throws IOException, InterruptedException, GitAPIException, GitConfigException {
//        String userDir = System.getProperty("user.dir");
//        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");
//
//        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
//        Process process = pb.start();
//        int ret = process.waitFor();
//
//        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
//        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
//        commitCommand.commit("Commit message.", false);
//
//        // コミットが実行されなかったために、--amend のメッセージが存在していないのが正しい。
//        //assertNull(commitCommand.readCommitEditMsg());
//    }
//
//    @Test
//    public void testCommit_コミットメッセージが空のときはNoMessageExceptionがスローされる() throws IOException, InterruptedException, GitAPIException {
//        String userDir = System.getProperty("user.dir");
//        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");
//
//        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
//        Process process = pb.start();
//        int ret = process.waitFor();
//
//        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
//        GitCommitCommand commitCommand = new GitCommitCommand(workDir);
//        assertThrows(NoMessageException.class, () -> commitCommand.commit("", false));
//    }
//
//    @Test
//    public void testCommit_コンフリクトが解消されていない時にコミットを実行した場合はWrongRepositoryStateExceptionがスローされる() throws IOException, InterruptedException, GitAPIException {
//        String userDir = System.getProperty("user.dir");
//        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitConflict.sh");
//
//        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
//        Process process = pb.start();
//        int ret = process.waitFor();
//
//        File workDir = Paths.get(userDir, "src/test/resources/work/bob").toFile();
//        GitCommitCommand commitCommand = new GitCommitCommand(workDir);
//        assertThrows(WrongRepositoryStateException.class, () -> commitCommand.commit("Commit message.", /*bAmend=*/ false));
//    }
//
//    @Test
//    // git commit 時に hook script でエラーになった場合は、AbortedByHookException がスローされる。
//    public void testCommit_HookScriptError() throws IOException, InterruptedException {
//        String userDir = System.getProperty("user.dir");
//        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitFail.sh");
//
//        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
//        Process process = pb.start();
//        int ret = process.waitFor();
//
//        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
//        GitCommitCommand commitCommand = new GitCommitCommand(workDir);
//        AbortedByHookException e = assertThrows(AbortedByHookException.class, () -> commitCommand.commit("Commit message.", /*bAmend=*/ false));
//        assertEquals("Rejected by \"pre-commit\" hook.\nerror\n", e.getMessage());
//    }
}
