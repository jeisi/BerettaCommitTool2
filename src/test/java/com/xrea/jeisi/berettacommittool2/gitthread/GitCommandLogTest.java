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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class GitCommandLogTest {

    public GitCommandLogTest() {
    }

    @Test
    public void testGetLog() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testAmend.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        File workDir = Paths.get(userDir, "src/test/resources/work/alice").toFile();
        GitLogCommand command = new GitLogCommand(workDir);

    }
}
