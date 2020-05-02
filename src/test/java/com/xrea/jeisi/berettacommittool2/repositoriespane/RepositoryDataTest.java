/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class RepositoryDataTest {

    @Test
    public void testToString() {
        RepositoryData data = new RepositoryData(true, "beretta", Paths.get("beretta"));
        
        assertThat(data.toString()).isEqualTo("{beretta, true}");
    }
    
    @Test
    public void testToString_配列時の表示() {
        ArrayList<String> repositories = new ArrayList<>();
        repositories.add("beretta");
        repositories.add("beretta/gyp");

        ObservableList<RepositoryData> datas = FXCollections.observableArrayList();
        repositories.forEach((repository) -> {
            datas.add(new RepositoryData(true, repository, Paths.get(repository)));
        });
        
        assertThat(datas.toString()).isEqualTo("[{beretta, true}, {beretta/gyp, true}]");
    }
}
