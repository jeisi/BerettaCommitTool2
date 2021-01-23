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

/**
 *
 * @author jeisi
 */
public class GitCheckoutCommand extends BaseMultiGitCommand {

    public GitCheckoutCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void checkoutHead(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> execProcess("git", "checkout", "--", data.getFileName()));
    }

    public void checkoutHead(GitStatusData data) throws IOException, GitConfigException, InterruptedException {
        checkoutHead(Arrays.asList(data));
    }

    public void checkoutOurs(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> {
            String file = data.getFileName();
            if (data.getIndexStatus().equals("D")) {
                execProcess("git", "rm", "-f", file);
            } else {
                execProcess("git", "checkout", "--ours", file);
                execProcess("git", "add", file);
            }
        });
    }

    public void checkoutTheirs(List<GitStatusData> datas) throws IOException, GitConfigException, InterruptedException {
        execEachFile(datas, (data) -> {
            String file = data.getFileName();
            if (data.getWorkTreeStatus().equals("D")) {

            } else {
                execProcess("git", "checkout", "--theirs", file);
                execProcess("git", "add", file);
            }
        });
    }

}
