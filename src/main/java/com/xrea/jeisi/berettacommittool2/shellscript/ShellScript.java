/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.shellscript;

import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 *
 * @author jeisi
 */
public class ShellScript {

    private OutputStream outputStream = new ByteArrayOutputStream();
    private final File workDir;
    private ExecuteResultHandler resultHandler;

    public ShellScript(File workDir) {
        this.workDir = workDir;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void setResultHandler(ExecuteResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    public int exec(String exe, String[] options/*, List<String> displayCommand*/) throws IOException {
        //PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, outputStream);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        int result = execCommon(exe, options, streamHandler, /*handleQuoting=*/ false);
        return result;
    }

    public String[] execWithOutput(String exe, String[] options /*, List<String> displayCommand,*/) throws IOException {
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

    private int execCommon(String exe, String[] options, PumpStreamHandler streamHandler, boolean handleQuoting) throws IOException {
        XmlWriter.writeStartMethod("ShellScript.execCommon(%s %s)", exe, Arrays.toString(options));
        //printCommandName(displayCommand, workDir, out);

        CommandLine commandLine = new CommandLine(exe);
        commandLine.addArguments(options);

        Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workDir);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        if (resultHandler == null) {
            int ret = executor.execute(commandLine);
            XmlWriter.writeEndMethodWithReturnValue(ret);
            return ret;
        } else {
            executor.execute(commandLine, resultHandler);
            XmlWriter.writeEndMethodWithReturnValue(0);
            return 0;
        }
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