/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import java.io.IOException;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class ExeCreatorWin extends ExeCreator {

    public ExeCreatorWin(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void exec() throws IOException {
        List<ProgramInfo> programInfos = new ArrayList<>();
        programInfos.add(new ProgramInfo("git", "git.exe", new String[]{"c:/Program Files/Git/bin/git.exe"}));
        programInfos.add(new ProgramInfo("bash", "bash.exe", new String[]{"c:/Program Files/Git/bin/bash.exe"}));
        programInfos.add(new ProgramInfo("gitk", "gitk", new String[]{"c:/Program Files/Git/mingw64/bin/gitk"}));
        programInfos.add(new ProgramInfo("WinMergeU", "WinMergeU.exe", new String[]{"c:/Program Files/WinMerge/WinMergeU.exe"}));
        SetUpWizard wizard = new SetUpWizard(configInfo, programInfos);
        if (wizard.getNullPrograms().size() > 0) {
            //Platform.runLater(() -> wizard.exec());
            wizard.exec();
        }

        createExecFile("winmerge.sh");

        String difftool = configInfo.getDiffTool();
        if (difftool == null) {
            configInfo.setDiffTool("winmerge");
        }
        String mergetool = configInfo.getMergeTool();
        if (mergetool == null) {
            configInfo.setMergeTool("winmerge");
        }
    }
}
