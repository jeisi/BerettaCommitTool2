/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.exception.GitConfigException;
import com.xrea.jeisi.berettacommittool2.gitthread.GitConfigCommand;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class ExeCreatorMac extends ExeCreator {

    public ExeCreatorMac(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void exec() throws IOException, InterruptedException, GitConfigException {
        List<ProgramInfo> programInfos = new ArrayList<>();
        programInfos.add(new ProgramInfo("git", "git", new String[]{"/usr/local/bin/git"}));
        programInfos.add(new ProgramInfo("gitk", "gitk", new String[]{"/usr/local/bin/gitk"}));
        programInfos.add(new ProgramInfo("p4merge", "p4merge.app", new String[]{"/Applications/p4merge.app"}));
        SetUpWizard wizard = new SetUpWizard(configInfo, programInfos);
        if (wizard.getNullPrograms().size() > 0) {
            wizard.exec();
        }

        String difftool = configInfo.getDiffTool();
        if (difftool == null) {
            configInfo.setDiffTool("p4merge");
        }

        Path homeDir = Paths.get(System.getProperty("user.home"));
        GitConfigCommand configCommand = new GitConfigCommand(homeDir, configInfo);
        configCommand.list();
        String value = configCommand.getValue("difftool.p4merge.cmd");
        String expected = String.format("%s/Contents/Resources/launchp4merge \"$LOCAL\" \"$REMOTE\"", configInfo.getProgram("p4merge", ""));
        if (value == null || !value.equals(expected)) {
            configCommand.setValue("difftool.p4merge.cmd", expected, "--global");
        }
    }
}
