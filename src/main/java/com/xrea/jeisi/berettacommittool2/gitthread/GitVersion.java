/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.exception.GitIllegalVersionException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class GitVersion extends BaseSingleGitCommand {
    
    public GitVersion(ConfigInfo configInfo) {
        super(Paths.get(System.getProperty("user.home")), configInfo);
    }

    public VersionInfo getVersion() throws GitConfigException, IOException, InterruptedException {
        String[] lines = execProcess("git", "--version");
        
        Pattern p = Pattern.compile("git version (\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher m = p.matcher(lines[0]);
        if(!m.matches()) {
            List<String> message = new ArrayList<>();
            message.add("git --version の出力結果が想定外の値です:");
            message.add(lines[0]);            
            throw new GitIllegalVersionException(message);
        }
        
        int majorVersion = Integer.parseInt(m.group(1));
        int minorVersion = Integer.parseInt(m.group(2));
        int patchVersion = Integer.parseInt(m.group(3));
        return new VersionInfo(majorVersion, minorVersion, patchVersion);
    }
}
