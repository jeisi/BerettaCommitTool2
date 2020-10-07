/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public abstract class BaseGitCommand {

    protected static String getErrorMessage(List<String> command, Process p) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("command error.");
        sb.append("\n");
        sb.append("$ ");
        sb.append(String.join(" ", command));
        sb.append("\n");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getErrorStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    protected static List<String> getInputStream(Process p) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
        }
        return lines;
    }
}
