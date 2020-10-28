/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.shellscript;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 *
 * @author jeisi
 */
public class ShellScript {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final File workDir;

    public ShellScript(File workDir) {
        this.workDir = workDir;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public int exec(String exe, String[] options/*, List<String> displayCommand*/) throws IOException {
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, outputStream);
        int result = execCommon(exe, options, /*displayCommand,*/ workDir, streamHandler, outputStream, /*handleQuoting=*/ false);
        return result;
    }

    public String[] execWithOutput(String exe, String[] options /*, List<String> displayCommand,*/ ) throws IOException {
        CommandLine commandLine = new CommandLine(exe);
        commandLine.addArguments(options);

        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        //printCommandName(displayCommand, workDir, outputStream);

        Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workDir);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        executor.execute(commandLine);
        String outputString = outputStream.toString();
        if (outputString.isEmpty()) {
            return new String[0];
        } else {
            return outputString.split("\\n");
        }
    }

    private static int execCommon(String exe, String[] options, /*List<String> displayCommand,*/ File workDir, PumpStreamHandler streamHandler, OutputStream out, boolean handleQuoting) throws IOException {
        //printCommandName(displayCommand, workDir, out);

        CommandLine commandLine = new CommandLine(exe);
        commandLine.addArguments(options);

        Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workDir);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(commandLine);
        return exitValue;
    }

    /*
    private static void printCommandName(List<String> args, File workDir, OutputStream out) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(workDir.toString());
        builder.append("]\n");
        builder.append("$ ");
        builder.append(String.join(" ", args));
        builder.append("\n");
        out.write(builder.toString().getBytes());
        out.flush();
    }
    */

}
