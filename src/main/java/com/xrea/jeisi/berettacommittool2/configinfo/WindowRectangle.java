/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.configinfo;

/**
 *
 * @author jeisi
 */
public class WindowRectangle {
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final boolean isValidXY;
    
    public WindowRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        isValidXY = true;
    }

    public WindowRectangle(double width, double height) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        isValidXY = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    public boolean isValidXY() {
        return isValidXY;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if(isValidXY) {
            builder.append(x);
            builder.append(", ");
            builder.append(y);
            builder.append(", ");
        }
        builder.append(width);
        builder.append(", ");
        builder.append(height);
        builder.append("}");
        return builder.toString();
    }
}
