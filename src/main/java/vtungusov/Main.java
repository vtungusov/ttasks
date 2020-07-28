package vtungusov;

import vtungusov.parser.FileParser;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1 && args[0] != null && args[1] != null) {
            String inputFileName = args[0];
            String outputFileName = args[1];
            FileParser fileParser = new FileParser(inputFileName);

            try {
                fileParser
                        .getSymbolFrequencyReport()
                        .printToFile(outputFileName);
            } catch (FileNotFoundException e) {
                System.out.println("Input file not found");
            } catch (IOException e) {
                System.out.println("File reading error");
            }

        } else
            System.out.println("Provide input and output file names in arguments (first - input, second - output)");
    }
}
