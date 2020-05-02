/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class GitThread extends Thread {

    private LinkedList<Runnable> commands = new LinkedList<>();
    private boolean alive = true;
    private boolean sleeping = false;

    public synchronized void addCommand(Runnable command) {
        //System.out.println("GitThread.addCommand()");
        commands.add(command);
        if (sleeping) {
            interrupt();
        }
    }

    public synchronized boolean isActive() {
        return !sleeping;
    }
    
    public synchronized void close() {
        //System.out.println("gitThread.close()");
        commands.clear();
        alive = false;
        if (sleeping) {
            interrupt();
        }
    }

    public synchronized List<Runnable> getCommands() {
        return commands;
    }

    @Override
    public void run() {
        while (alive) {
            if (commands.size() > 0) {
                Runnable command = getCurrentCommand();
                command.run();
            } else {
                //System.out.println("loop sleep");
                setSleeping(true);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // interrupt
                    //System.out.println("InterruptedException");
                }
                setSleeping(false);
                //System.out.println("loop end");
            }
        }
    }

    private synchronized Runnable getCurrentCommand() {
        return commands.poll();
    }

    private synchronized void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }
}
