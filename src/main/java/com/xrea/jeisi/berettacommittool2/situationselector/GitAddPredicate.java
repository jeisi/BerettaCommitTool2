/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.situationselector;

import com.xrea.jeisi.berettacommittool2.gitstatuspane.GitStatusData;
import java.util.function.Predicate;

/**
 *
 * @author jeisi
 */
public class GitAddPredicate implements Predicate<GitStatusData> {

    @Override
    public boolean test(GitStatusData t) {
        if(t == null) {
            return false;
        }
        switch (t.workTreeStatusProperty().get()) {
            case "M":
            case "D":
            case "?":
                return true;
            default:
                return false;
        }
    }
    
}
