/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.xmlwriter.XmlWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeisi
 */
public class ExeCreatorUnix extends ExeCreator {

    public ExeCreatorUnix(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void exec() {
        List<ProgramInfo> programInfos = new ArrayList<>();
        programInfos.add(new ProgramInfo("git", "git", new String[]{"/usr/bin/git"}));
        programInfos.add(new ProgramInfo("gitk", "gitk", new String[]{"/usr/bin/gitk"}));
        SetUpWizard wizard = new SetUpWizard(configInfo, programInfos);
        if (wizard.getNullPrograms().size() > 0) {
            wizard.exec();
        }

        String difftool = configInfo.getDiffTool();
        if (difftool == null) {
            configInfo.setDiffTool("meld");
        }
    }
}
