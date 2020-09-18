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
import javafx.application.Platform;

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
        XmlWriter.writeStartMethod("ExeCreatorWin.exec()");

        createExecFile("winmerge.sh");

        String difftool = configInfo.getDiffTool();
        if (difftool == null) {
            configInfo.setDiffTool("winmerge");
        }

        List<String> programs = new ArrayList();
        programs.add("git");
        programs.add("WinMergeU");
        SetUpWizard wizard = new SetUpWizard(configInfo, programs);
        if (wizard.getNullPrograms().size() > 0) {
            //Platform.runLater(() -> wizard.exec());
            wizard.exec();
        }

        XmlWriter.writeEndMethod();
    }
}
