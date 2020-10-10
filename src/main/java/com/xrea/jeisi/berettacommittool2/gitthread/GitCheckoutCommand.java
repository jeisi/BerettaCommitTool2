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

/**
 *
 * @author jeisi
 */
public class GitCheckoutCommand extends BaseGitCommand {

    public GitCheckoutCommand(Path repository, ConfigInfo configInfo) {
        super(repository, configInfo);
    }

    public void checkoutHead(String... files) throws IOException, GitConfigException, InterruptedException {
        XmlWriter.writeStartMethod("GitCheckoutCommand.checkoutHead()");
        
        execEachFile(files, (file) -> execProcess("git", "checkout", "--", file));

        XmlWriter.writeEndMethod();
    }

    public void checkoutOurs(String... files) throws IOException, GitConfigException, InterruptedException {
        execEachFile(files, (file) -> {
            execProcess("git", "checkout", "--ours", file);
            execProcess("git", "add", file);
        });
    }

    public void checkoutTheirs(String... files) throws IOException, GitConfigException, InterruptedException {
        execEachFile(files, (file) -> {
            execProcess("git", "checkout", "--theirs", file);
            execProcess("git", "add", file);
        });
    }

}
