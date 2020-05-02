/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author jeisi
 */
@ExtendWith(ApplicationExtension.class)
public class StyleTableCellTest {
    
    public StyleTableCellTest() {
    }

    @Test
    public void testError() {
        StyleTableCell cell = new StyleTableCell();
        cell.updateItem("beretta [error! repository not found: repository not found: /home/jeisi/test/git/sandbox/work/beretta/beretta/gyp]", false);
        
        // エラー時は赤色で描画。
        assertEquals("-fx-text-fill:red;", cell.getStyle());
    }
    
    @Test
    public void testUpdating() {
        StyleTableCell cell = new StyleTableCell();
        cell.updateItem("beretta [updating...]", false);
        
        // 更新中は黄色で描画。
        assertEquals("-fx-text-fill:yellow;", cell.getStyle());
        
    }
}
