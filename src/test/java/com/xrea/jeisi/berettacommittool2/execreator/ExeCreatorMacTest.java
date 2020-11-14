/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class ExeCreatorMacTest {
    
    public ExeCreatorMacTest() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of exec method, of class ExeCreatorMac.
     */
    @Test
    public void testExec() throws Exception {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("git", "/usr/bin/git");
        configInfo.setProgram("gitk", "/usr/bin/gitk");
        configInfo.setProgram("p4merge", "/Applications/p4merge.app");
        
        ExeCreatorMac creator = new ExeCreatorMac(configInfo);
        creator.exec();
    }
    
}
