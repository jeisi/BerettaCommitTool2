/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import static java.lang.Thread.interrupted;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class GitThreadTest {

    private boolean isCommand1Execed;
    private boolean isCommand2Execed;
    private boolean isInterrupted;
    
    public GitThreadTest() {
    }

    @Test
    public void test() throws InterruptedException {
        isCommand1Execed = false;
        isCommand2Execed = false;
        isInterrupted = false;
        
        GitThread gitThread = new GitThread();
        gitThread.start();
        Thread.sleep(1500);
        gitThread.addCommand(() -> {
            //System.out.println("command1 start.");
            try {
                Thread.interrupted();
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                Logger.getLogger(GitThreadTest.class.getName()).log(Level.SEVERE, null, ex);
                isInterrupted = true;
            }
            //System.out.println("command1 end.");
            isCommand1Execed = true;
        });
        Thread.sleep(1500);
        gitThread.addCommand(() -> {
            //System.out.println("command2");
            isCommand2Execed = true;
        });
        
        while(gitThread.getCommands().size() > 0) {
            Thread.sleep(1000);
        }
        
        gitThread.close();        
        while(gitThread.isAlive());

        assertTrue(isCommand1Execed);
        assertTrue(isCommand2Execed);
        assertFalse(isInterrupted);
    }
}
