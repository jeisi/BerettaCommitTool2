/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.util.HashMap;

/**
 *
 * @author jeisi
 */
public class GitThreadMan {
    private static final HashMap<String, GitThread> gitThreads = new HashMap<>();
    
    public static GitThread get(String directory) {
        GitThread gitThread = gitThreads.get(directory);
        if(gitThread == null) {
            gitThread = new GitThread();
            gitThreads.put(directory, gitThread);
            gitThread.start();
            //System.out.println(String.format("gitThread[%s].start()", directory));
        }
        return gitThread;
    }
    
    public static boolean isActiveAny() {
        return gitThreads.values().stream().filter(e -> e.isActive()).count() > 0;
    }
    
    public static void closeAll() {
        gitThreads.values().forEach(e -> e.close());
    }
}
