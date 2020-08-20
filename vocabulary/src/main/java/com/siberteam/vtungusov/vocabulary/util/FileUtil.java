package com.siberteam.vtungusov.vocabulary.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static void checkInputFile(String inputFileName) throws IOException {
        Path path = Paths.get(inputFileName);
        if (Files.notExists(path)) {
            throw new FileNotFoundException("File not found");
        }
        if (!Files.isReadable(path)) {
            throw new IOException("Can`t read the file, access denied");
        }
        if (Files.size(path) < 1) {
            throw new IOException("Input file is empty");
        }
    }

    public static void checkOutputFile(String inputFileName) throws IOException {
        Path path = Paths.get(inputFileName);
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new IOException("Can`t create report file, access denied");
            }
        }

        if (!Files.isWritable(path)) {
            throw new IOException("Can`t write to the file, access denied");
        }
    }
}
