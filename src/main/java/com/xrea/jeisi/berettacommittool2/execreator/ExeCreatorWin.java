/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import java.io.IOException;
import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;

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
        createExecFile("winmerge.sh");
        
        String difftool = configInfo.getDiffTool();
        if (difftool == null) {
            configInfo.setDiffTool("winmerge");
        }
    }
}
