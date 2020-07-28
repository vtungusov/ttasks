package vtungusov;

import vtungusov.parser.FileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        String fileName;
        fileName = readFileName();
        FileParser fileParser = new FileParser(new File(fileName));
        try {
            fileParser.getSymbolFrequencyReport().printToConsole();
        } catch (IOException e) {
            System.out.println("File reading error");
        }
    }

    private static String readFileName() {
        String fileName = null;
        System.out.println("Enter file name:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            fileName = reader.readLine();
        } catch (IOException e) {
            System.out.println("Error during read file name");
        }
        return fileName;
    }
}
