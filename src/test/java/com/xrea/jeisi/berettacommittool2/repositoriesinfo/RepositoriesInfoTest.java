/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriesinfo;

import com.xrea.jeisi.berettacommittool2.repositoriespane.RepositoryData;
import java.util.Arrays;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class RepositoriesInfoTest {
    
    public RepositoriesInfoTest() {
    }

    @Test
    public void hello() {
        RepositoriesInfo info = new RepositoriesInfo((ObservableList<RepositoryData>)null);
        info.setRepositories(Arrays.asList(".", "gyp", "gyptools"), "beretta");
        
        // デフォルトでは全てにチェックがついている状態。
        assertEquals("[{., true}, {gyp, true}, {gyptools, true}]", info.getChecked().toString());
        
        // チェックを外したら getChecked() の値も変わる。
        info.getData(0).checkProperty().set(false);
        assertEquals("[{gyp, true}, {gyptools, true}]", info.getChecked().toString());
        
    }
}
