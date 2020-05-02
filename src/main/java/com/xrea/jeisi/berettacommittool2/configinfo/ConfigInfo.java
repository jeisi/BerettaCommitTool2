/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author jeisi
 */
public class ConfigInfo {

    private Path configFile;
    private HashMap<String, Object> map = new HashMap<>();

    public ConfigInfo() {
        configFile = Paths.get(System.getProperty("user.home"), ".BerettaCommitTool2", "config.yaml");
    }

    public void setConfigFile(Path configFile) {
        this.configFile = configFile;
    }

    public void setDirectoryHistory(List<String> directoryHistory) {
        map.put("directoryHistory", directoryHistory);
    }

    public List<String> getDirectoryHistory() {
        return (List<String>) map.get("directoryHistory");
    }
    
    public void setCommitMessageHistory(List<String> commitMessageHistory) {
        map.put("commitMessageHistory", commitMessageHistory);
    }
    
    public List<String> getCommitMessageHistory() {
        return (List<String>) map.get("commitMessageHistory");
    }

    public void save() throws IOException {
        pruneDirectoryHistory();

        Path parentDirectory = configFile.getParent();
        if (!Files.exists(parentDirectory)) {
            Files.createDirectories(parentDirectory);
        }

        Yaml yaml = new Yaml();
        try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
            yaml.dump(map, writer);
        }
    }

    public void load() throws IOException {
        Path parentDirectory = configFile.getParent();
        if (!Files.exists(parentDirectory)) {
            return;
        }
        
        if(!Files.exists(configFile)) {
            return;
        }

        Yaml yaml = new Yaml();
        try (BufferedReader reader = Files.newBufferedReader(configFile)) {
            map = yaml.load(reader);
        }
    }

    private void pruneDirectoryHistory() {
        getDirectoryHistory().removeIf(e -> !Files.exists(Paths.get(e)));
    }
}
