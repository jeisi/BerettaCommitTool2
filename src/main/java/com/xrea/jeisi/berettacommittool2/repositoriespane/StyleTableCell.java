/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.repositoriespane;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TableCell;

/**
 *
 * @author jeisi
 */
public class StyleTableCell extends TableCell<RepositoryData, String> {
    private static final Pattern modifiedPattern = Pattern.compile(".+ \\(\\d+\\)");
    private static Pattern errorPattern = Pattern.compile(".+ \\[error! .+\\]");
    private static Pattern updatingPattern = Pattern.compile(".+ \\[updating...\\]");
    
    @Override
    public void updateItem(final String item, final boolean empty) {
        super.updateItem(item, empty);//*don't forget!
        if (item != null) {
            setText(item);
            Matcher matcher = modifiedPattern.matcher(item);
            if(matcher.matches()) {
                setStyle("-fx-text-fill:blue; -fx-font-weight: bolder; -fx-font-family: sans-serif ;");
            } else if(updatingPattern.matcher(item).matches()) {
                setStyle("-fx-text-fill:yellow;");
            } else if(errorPattern.matcher(item).matches()) {
                //setStyle("-fx-font-color:red;");
                setStyle("-fx-text-fill:red;");
            } else {
                setStyle(null);                
            }
        } else {
            setText(null);
        }
    }
}
