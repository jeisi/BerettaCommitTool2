/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class GitCommandDiffTest {

    public GitCommandDiffTest() {
    }

    @Test
    // meld が立ち上がるかどうかのテスト。
    public void testDiff() throws IOException, InterruptedException, GitCommandException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiff.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitDiffCommand diffCommand = new GitDiffCommand(workDir);
        diffCommand.diff("a.txt");
    }

    @Test
    // git difftool がエラー終了した時は GitCommandException がスローされる
    public void testGitCommandException() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiff.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitDiffCommand diffCommand = new GitDiffCommand(workDir) {
            @Override
            protected List<String> getCommand(String fileName, boolean bCached) {
                return Arrays.asList(new String[]{"git", "difftool", "-y", "--tool=vimdiff", fileName});
            }
        };
        GitCommandException e = assertThrows(GitCommandException.class, () -> diffCommand.diff("a.txt"));
        String expect = "+ git difftool -y --tool=vimdiff a.txt\n"
                + "The diff tool vimdiff is not available as 'vim'\n"
                + "fatal: external diff died, stopping at a.txt\n";
        assertEquals(expect, e.getMessage());
    }

    @Test
    // diffCached() コマンドを実行したら git difftool -y --tool=meld --cached が実行される。
    public void testDiffCached() throws IOException, InterruptedException, GitCommandException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testDiffCached.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/beretta").toFile();
        GitDiffCommand diffCommand = new GitDiffCommand(workDir);
        diffCommand.diffCached("a.txt");
    }
}
