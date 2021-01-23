/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.gitthread;

/**
 *
 * @author jeisi
 */
public class VersionInfo implements Comparable<VersionInfo> {
    final int major;
    final int minor;
    final int patch;
    
    public VersionInfo(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    @Override
    public boolean equals(Object rhs) {
        if(this == rhs) {
            return true;
        }
        if(rhs instanceof VersionInfo) {
            VersionInfo v2 = (VersionInfo) rhs;
            return major == v2.major && minor == v2.minor && patch == v2.patch;
        }
        return false;
    }

    @Override
    public int compareTo(VersionInfo t) {
        if(major != t.major) {
            return (major < t.major) ? -1 : 1;
        }
        if(minor != t.minor) {
            return (minor < t.minor) ? -1 : 1;
        }
        if(patch != t.patch) {
            return (patch < t.patch) ? -1 : 1;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
