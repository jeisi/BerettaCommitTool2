/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.execreator.ProgramInfo;
import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author jeisi
 */
public class GitCommandVersionTest {

    private ConfigInfo configInfo;

    public GitCommandVersionTest() {
    }

    @BeforeEach
    public void setUp() {
        ProgramInfo programInfo = new ProgramInfo("git", "git", new String[]{"/usr/bin/git"});
        configInfo = new ConfigInfo();
        configInfo.setupDefaultProgram(programInfo);
    }

    @Test
    public void testVersion() throws GitConfigException, InterruptedException, IOException {
        GitVersion gitVersion = new GitVersion(configInfo);

        VersionInfo expected = new VersionInfo(2, 25, 1);
        VersionInfo actual = gitVersion.getVersion();
        assertEquals(expected, actual);
    }

    @Test
    public void testCompare() {
        VersionInfo a = new VersionInfo(1, 1, 1);
        VersionInfo b = new VersionInfo(2, 0, 0);
        assertEquals(-1, a.compareTo(b));

        VersionInfo c = new VersionInfo(2, 1, 1);
        VersionInfo d = new VersionInfo(2, 0, 0);
        assertEquals(1, c.compareTo(d));

        VersionInfo e = new VersionInfo(2, 1, 1);
        VersionInfo f = new VersionInfo(2, 1, 0);
        assertEquals(1, e.compareTo(f));

        VersionInfo g = new VersionInfo(2, 1, 1);
        VersionInfo h = new VersionInfo(2, 1, 1);
        assertEquals(0, g.compareTo(h));
    }
}
