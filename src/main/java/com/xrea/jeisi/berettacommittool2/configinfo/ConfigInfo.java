/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    //private static ConfigInfo instance = new ConfigInfo();
    
    public ConfigInfo() {
        configFile = Paths.get(System.getProperty("user.home"), ".BerettaCommitTool2", "config.yaml");
    }

    public void setConfigFile(Path configFile) {
        this.configFile = configFile;
    }

    public Path getPath() {
        return configFile;
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
    
    public void setWindowRectangle(String windowName, double x, double y, double width, double height) {
        XmlWriter.writeStartMethod(String.format("ConfigInfo.setWindowRectangle(%s, %f, %f, %f, %f", windowName, x, y, width, height));

        //map.put(windowName + ".rectangle", new double[]{x, y, width, height});
        List<Double> r = new ArrayList<>();
        r.add(x);
        r.add(y);
        r.add(width);
        r.add(height);
        map.put(windowName + ".rectangle", r);
        
        XmlWriter.writeEndMethod();
    }

    public void setWindowRectangle(String windowName, WindowRectangle windowRectangle) {
        setWindowRectangle(windowName, windowRectangle.getX(), windowRectangle.getY(), windowRectangle.getWidth(), windowRectangle.getHeight());
    }
    
    public WindowRectangle getWindowRectangle(String windowName) {
        List<Double> r = (List<Double>) map.get(windowName + ".rectangle");
        if(r == null) {
            return null;
        }
        return new WindowRectangle(r.get(0), r.get(1), r.get(2), r.get(3));
    }
    
    public void setProgram(String name, String path) {
        map.put("program." + name, path);
    }
    
    public String getProgram(String name) {
        return (String) map.get("program." + name);
    }
    
    public void setDiffTool(String difftool) {
        map.put("difftool", difftool);
    }
    
    public String getDiffTool() {
        String difftool = (String) map.get("difftool");
        return difftool;
    }
    
    public void setTableColumnWidth(String tableId, List<Double> widths) {
        map.put(tableId + ".columnWidths", widths);
    }
    
    public List<Double> getTableColumnWidth(String tableId) {
        return (List<Double>) map.get(tableId + ".columnWidths");
    }
    
    public void setDouble(String key, double value) {
        map.put(key, value);
    }
    
    public Double getDouble(String key) {
        return (Double) map.get(key);
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
        List<String> directoryHistory = getDirectoryHistory();
        if(directoryHistory == null) {
            return;
        }
        directoryHistory.removeIf(e -> !Files.exists(Paths.get(e)));
    }
}
