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
    
    public WindowRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(x);
        builder.append(", ");
        builder.append(y);
        builder.append(", ");
        builder.append(width);
        builder.append(", ");
        builder.append(height);
        builder.append("}");
        return builder.toString();
    }
}
