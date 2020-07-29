package vtungusov;

import vtungusov.parser.FileParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1 && args[0] != null && args[1] != null) {
            String inputFileName = args[0];
            String outputFileName = args[1];

            try {
                new FileParser(Files.lines(Paths.get(inputFileName)))
                        .getSymbolFrequencyReport()
                        .printToFile(outputFileName);
            } catch (IOException e) {
                System.out.println("File reading error");
            }

        } else
            System.out.println("Provide input and output file names in arguments (first - input, second - output)");
    }
}
