/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author jeisi
 */
public class JUtilityTest {

    public JUtilityTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    // C:/user のような Windows の絶対パスのときのテスト
    public void testExpandPathWindows() {
        Path actual = JUtility.expandPath("beretta", "c:/test");
        Path expected = Paths.get("c:/test");
        assertEquals(expected, actual);
    }

    @Test
    // /test のような Mac の絶対パスの時のテスト
    public void testExpandPathMac() {
        Path actual = JUtility.expandPath("beretta", "/test");
        Path expected = Paths.get("/test");
        assertEquals(expected, actual);
    }
    
    @Test
    // ~/test のようなホームディレクトリからのパスの時のテスト
    public void testExpandPathHome() {
        Path actual = JUtility.expandPath("beretta", "~/test");
        Path expected = Paths.get("~/test");
        assertEquals(expected, actual);        
    }
}
