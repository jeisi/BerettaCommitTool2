/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitcommitwindow;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class GitCommitPaneTest {
    
    public GitCommitPaneTest() {
    }

    @Test
    public void testRemoveComment() {
        String originalMessage = 
            "Merge branch 'master' of /home/jeisi/test/git/sandbox/git-depot/beretta_gyp\n" +
            "\n" +
            "# Conflicts:\n" +
            "#	a.txt\n" +
            "#	sources_common_all.txt";

        String expected = 
            "Merge branch 'master' of /home/jeisi/test/git/sandbox/git-depot/beretta_gyp\n" +
            "\n" +
            "Conflicts:\n" +
            "	a.txt\n" +
            "	sources_common_all.txt";
        String actual = GitCommitPane.removeComment(originalMessage);
        assertEquals(expected, actual);
    }
}
