/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class ConfigInfoTest {

    public ConfigInfoTest() {
    }

    @Test
    public void testSave() throws IOException {
        ConfigInfo saveConfigInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources", ".BerettaCommitTool2", "config.yaml");
        saveConfigInfo.setConfigFile(configFile);

        ObservableList<String> directoryHistory = FXCollections.observableArrayList("/home/jeisi/Downloads", "/home/jeisi/Music");
        saveConfigInfo.setDirectoryHistory(directoryHistory);

        saveConfigInfo.save();

        ConfigInfo loadConfigInfo = new ConfigInfo();
        loadConfigInfo.setConfigFile(configFile);
        loadConfigInfo.load();
        assertThat("[/home/jeisi/Downloads, /home/jeisi/Music]").isEqualTo(loadConfigInfo.getDirectoryHistory().toString());
    }

    @Test
    public void testPrune() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testPrune.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ConfigInfo configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        var directoryHistory = new ArrayList<String>();
        directoryHistory.add(userDir + "/src/test/resources/work/Controls");
        directoryHistory.add(userDir + "/src/test/resources/work/Dummy");
        configInfo.setDirectoryHistory(directoryHistory);
        configInfo.save();

        // save() を実行した時に pruneDirectoryHistory() が実行され、Dummy ディレクトリは破棄される。
        assertThat(String.format("[%s]", userDir + "/src/test/resources/work/Controls")).isEqualTo(configInfo.getDirectoryHistory().toString());
    }

    @Test
    // ~/.BerettaCommitTool2/ が存在しない時は、何もしない。
    public void testLoad_NoBerettaCommitTool2Directory() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testPrune.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ConfigInfo configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources/work", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        configInfo.load();

        // ~/.BerettaCommitTool2/ が存在しないので、例外が発生せずに何も行われなければ OK。
    }

    @Test
    // ~/.BerettaCommitTool2/ は存在するけど、config.yaml が存在しない場合は何もしない。
    public void testLoad_NoYaml() throws IOException, InterruptedException {
        String userDir = System.getProperty("user.dir");
        Path bashCommand = Paths.get(userDir, "src/test/resources/testNoExistYaml.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();

        ConfigInfo configInfo = new ConfigInfo();
        Path configFile = Paths.get("/home/jeisi/NetBeansProjects/BerettaCommitTool2/src/test/resources/work", ".BerettaCommitTool2", "config.yaml");
        configInfo.setConfigFile(configFile);

        configInfo.load();

        // ~/.BerettaCommitTool2/config.yaml が存在しないので、例外が発生せずに何も行われなければ OK。
    }
}
