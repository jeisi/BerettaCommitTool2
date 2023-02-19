/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xrea.jeisi.berettacommittool2.convertcharset;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.input.BOMInputStream;

/**
 *
 * @author jeisi
 */
public class ConvertUtf8WithoutBomToUtf8WithBom {

    private Path path;

    public ConvertUtf8WithoutBomToUtf8WithBom(Path file) {
        this.path = file;
    }
    
    public ConvertUtf8WithoutBomToUtf8WithBom(String file) {
        this.path = Paths.get(file);
    }

    public void convert() throws IOException {
        Path outputPath = Paths.get(path.toString() + ".tmp");
        try (FileInputStream file = new FileInputStream(path.toFile()); BufferedInputStream input = new BufferedInputStream(file); BOMInputStream bomIn = new BOMInputStream(input); BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {

            //BOM付与
            out.write(0xef);
            out.write(0xbb);
            out.write(0xbf);

            int nSize;
            while (true) {
                byte[] buffer = new byte[65536];
                nSize = bomIn.read(buffer);
                if (nSize <= 0) {
                    break;
                }
                out.write(buffer, 0, nSize);
            }
        }
        
        Files.delete(path);
        Files.move(outputPath, path);
    }
}
