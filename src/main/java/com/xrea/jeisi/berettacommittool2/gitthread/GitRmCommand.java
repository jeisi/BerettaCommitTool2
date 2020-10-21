/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jeisi
 */
public class GitRmCommand extends BaseGitCommand {

    private Pattern pattern;

    public GitRmCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void rm(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> {
            if (data.indexStatusProperty().get().equals("R")) {
                if (pattern == null) {
                    pattern = Pattern.compile(".+ -> (.+)");
                    Matcher m = pattern.matcher(data.getFileName());
                    if (m.matches()) {
                        execProcess("git", "rm", "-f", m.group(1));
                    }
                }
            } else {
                execProcess("git", "rm", "-f", data.getFileName());
            }
        });
    }
    
    public void rm(GitStatusData data) throws IOException, GitConfigException, InterruptedException {
        rm(Arrays.asList(data));
    }
}
