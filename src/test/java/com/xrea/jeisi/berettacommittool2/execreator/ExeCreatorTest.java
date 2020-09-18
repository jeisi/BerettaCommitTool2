/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.execreator;

import com.xrea.jeisi.berettacommittool2.configinfo.ConfigInfo;
import com.xrea.jeisi.berettacommittool2.gitthread.GitCommandException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class ExeCreatorTest {

    public ExeCreatorTest() {
    }

    @Test
    public void test() throws IOException {
        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setProgram("WinMergeU", "c:/Program Files/WinMerge/WinMergeU.exe");
        ExeCreator app = new ExeCreatorWin(configInfo);
        app.createExecFile("winmerge.sh");
    }

    @Test
    // ConfigInfo に WinMergeU に対応するプログラムが指定されていない場合は FileNotFoundException がスローされる。
    public void testWithoutProgram() throws IOException {
        ConfigInfo configInfo = new ConfigInfo();
        ExeCreator app = new ExeCreatorWin(configInfo);
        assertThrows(FileNotFoundException.class, () -> app.createExecFile("winmerge.sh"));
    }
    
    @Test
    // 未登録のプログラムがない時はそのまま終了。
    public void testNoUnregistedProgram() throws IOException {

    }
}
