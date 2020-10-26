/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitCommandException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNoMessageException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitNothingAddedException;
import com.xrea.jeisi.berettacommittool2.exception.GitCommitUnmergedFilesException;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Test
    // readCommitEditMsg() 実行時に、COMMIT_EDIT_MSG ファイルが存在しない時は空文字列を返す。
    public void testReadCommitEditMsg_NoCommitEditMsg() throws IOException, InterruptedException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitFail.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
        assertEquals("", commitCommand.readCommitEditMsg());
    }

    @Test
    public void testCommit_ステージにファイルが登録されていなければGitCommitNothingAddedExceptionがスローされる() throws IOException, InterruptedException, GitConfigException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAdd.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);

        GitCommitNothingAddedException e = assertThrows(GitCommitNothingAddedException.class, () -> commitCommand.commit("Commit message.", /*bAmend=*/ false));
        String expected = "command error:\n"
                + "[/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources/work/beretta]\n"
                + "$ git commit\n"
                + "nothing added to commit but untracked files present\n";
        assertEquals(expected, e.getMessage());
    }

    @Test
    public void testCommit_コミットメッセージが空のときはGitCommitNoMessageExceptionがスローされる() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testUnstage.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
        assertThrows(GitCommitNoMessageException.class, () -> commitCommand.commit("", false));
    }

    @Test
    public void testCommit_コンフリクトが解消されていない時にコミットを実行した場合はGitCommitUnmergedFilesExceptionがスローされる() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitConflict.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/bob");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
        GitCommitUnmergedFilesException e = assertThrows(GitCommitUnmergedFilesException.class, () -> commitCommand.commit("Commit message.", /*bAmend=*/ false));
        String expected = "command error:\n"
                + "[/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources/work/bob]\n"
                + "$ git commit\n"
                + "error: Committing is not possible because you have unmerged files.\n"
                + "hint: Fix them up in the work tree, and then use 'git add/rm <file>'\n"
                + "hint: as appropriate to mark resolution and make a commit.\n"
                + "fatal: Exiting because of an unresolved conflict.\n";
        assertEquals(expected, e.getMessage());
    }

    @Test
    // git commit 時に hook script でエラーになった場合は、GitCommandException がスローされる。
    public void testCommit_HookScriptError() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testCommitFail.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        Path workDir = Paths.get(userDir, "src/test/resources/work/beretta");
        GitCommitCommand commitCommand = new GitCommitCommand(workDir, configInfo);
        GitCommandException e = assertThrows(GitCommandException.class, () -> commitCommand.commit("Commit message.", /*bAmend=*/ false));
        String expected = "command error:\n"
                + "[/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources/work/beretta]\n"
                + "$ git commit\n"
                + "error\n";
        assertEquals(expected, e.getMessage());
    }
}
