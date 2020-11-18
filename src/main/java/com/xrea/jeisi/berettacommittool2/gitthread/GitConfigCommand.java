/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class GitConfigCommand extends BaseSingleGitCommand {

    private Map<String, String> map;
    
    public GitConfigCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void list() throws IOException, InterruptedException, GitConfigException {
        XmlWriter.writeStartMethod("GitConfigCommand.list()");
        List<String> command = getListCommand();
        List<String> displayCommand = getListCommand();
        String[] lines = execProcess(command, displayCommand);
        
        map = new HashMap<>();
        Pattern p = Pattern.compile("(.*)=(.*)");
        for(String line : lines) {
            XmlWriter.writeObject("line", line);
            Matcher m = p.matcher(line);
            if(m.matches()) {
                String key = m.group(1);
                //XmlWriter.writeObject("key", key);
                String value = m.group(2);
                map.put(key, value);
            }
        }
        XmlWriter.writeEndMethod();
    }
    
    public String getValue(String key) {
        return map.get(key);
    }

    public void setValue(String key, String value, String... options) throws GitConfigException, IOException, InterruptedException {
        List<String> command = getSetCommand(key, value, options);
        List<String> displayCommand = getSetCommand(key, value, options);
        execProcess(command, displayCommand);
    }
    
    private List<String> getListCommand() {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("config");
        command.add("--list");
        return command;
    }
    
    private List<String> getSetCommand(String key, String value, String... options) {
        ArrayList<String> command = new ArrayList<>();
        command.add("git");
        command.add("config");
        command.addAll(Arrays.asList(options));
        command.add(key);
        command.add(value);
        return command;        
    }
}
