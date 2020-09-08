/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;

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
        String difftool = configInfo.getDiffTool();
        if(difftool == null) {
            configInfo.setDiffTool("meld");
        }
    }
}
