/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import java.io.IOException;
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
public class ConvertUtf8WithoutBomToUtf8WithBomTest {

    private String userDir = System.getProperty("user.dir");

    public ConvertUtf8WithoutBomToUtf8WithBomTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        Path bashCommand = Paths.get(userDir, "src/test/resources/testConvertUtf8WithoutBomToUtf8WithBom.sh");

        ProcessBuilder pb = new ProcessBuilder("bash", bashCommand.toString(), userDir);
        Process process = pb.start();
        int ret = process.waitFor();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of convert method, of class ConvertUtf8WithoutBomToUtf8WithBom.
     */
    @Test
    public void testConvert() throws Exception {
        System.out.println("convert");
        Path file = Paths.get(userDir, "src/test/resources/work/encode_utf8_without_bom.txt");
        ConvertUtf8WithoutBomToUtf8WithBom instance = new ConvertUtf8WithoutBomToUtf8WithBom(file);
        instance.convert();

        DetectCharset detectCharset = new DetectCharset();
        String actual = detectCharset.detect(file.toString());
        String expected = "UTF-8 with BOM";
        assertEquals(expected, actual);
    }

}
