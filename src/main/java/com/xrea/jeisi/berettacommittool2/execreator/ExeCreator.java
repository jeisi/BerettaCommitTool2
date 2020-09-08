/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public abstract class ExeCreator {

    ConfigInfo configInfo;

    public static ExeCreator create(ConfigInfo configInfo) {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            return new ExeCreatorWin(configInfo);
        } else if(os.contains("mac")) {
            return new ExeCreatorMac(configInfo);
        } else {
            return new ExeCreatorUnix(configInfo);
        }
    }
    
    
    protected ExeCreator(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    protected void createExecFile(String filename) throws IOException {
        List<String> lists = new ArrayList<>();
        Class<? extends ExeCreator> c = this.getClass();
        try (InputStream is = c.getResourceAsStream("/" + filename);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                lists.add(line);
            }
        }

        // 置換
        Pattern p = Pattern.compile("\\$\\{\\{(.+?)\\}\\}");
        for (int index = 0; index < lists.size(); ++index) {
            boolean bRetry;
            do {
                String line = lists.get(index);
                Matcher m = p.matcher(line);
                if (m.find()) {
                    String program = configInfo.getProgram(m.group(1));
                    if(program == null) {
                        throw new FileNotFoundException(m.group(1) + "に対応するプログラムが指定されていません。");
                    }
                    lists.set(index, m.replaceFirst(program));
                    bRetry = true;
                } else {
                    bRetry = false;
                }
            } while (bRetry);
        }

        Path binDir = Paths.get(configInfo.getPath().getParent().toString(), "bin");
        if (!Files.exists(binDir)) {
            Files.createDirectories(binDir);
        }

        Path outputFile = Paths.get(binDir.toString(), filename);
        Files.write(outputFile, lists);

        try {
            Files.setPosixFilePermissions(outputFile, PosixFilePermissions.fromString("rwxrwxr-x"));
        } catch (UnsupportedOperationException e) {
            // do nothing.
        }
    }
    
    public abstract void exec() throws IOException;
}
