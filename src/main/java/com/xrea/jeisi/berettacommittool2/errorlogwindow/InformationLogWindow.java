/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.errorlogwindow;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;

/**
 *
 * @author jeisi
 */
public class InformationLogWindow extends BaseLogWindow {

    public InformationLogWindow(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    protected String getIdentifier() {
        return "informationlogwindow";
    }

    @Override
    protected String getTitle() {
        return "Information";
    }
}
