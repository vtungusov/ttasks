package vtungusov;

import vtungusov.parser.FileParser;
import vtungusov.ui.UIManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static vtungusov.parser.FileParser.checkInputFile;
import static vtungusov.parser.FileParser.checkOutputFile;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report\n-----------------------------";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        if (uiManager.validateOptions(args)) {
            String[] handleArguments = uiManager.handleOptions();

            String inputFileName = handleArguments[0];
            String outputFileName = handleArguments[1];
            String lineCount = handleArguments[2];

            try {
                checkInputFile(inputFileName);
                checkOutputFile(outputFileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }

            try {
                FileParser fileParser = new FileParser();
                Stream<String> stringStream = Files.lines(Paths.get(inputFileName));

                if (lineCount == null) {
                    fileParser
                            .getSymbolFrequencyReport(stringStream)
                            .printToFile(outputFileName);
                } else {
                    fileParser
                            .getSymbolFrequencyReport(stringStream, Integer.parseInt(lineCount))
                            .printToFile(outputFileName);
                }
                System.out.println(SUCCESSFULLY_FINISHED);
            } catch (IOException e) {
                System.out.println("Something wrong, file reading error");
            }
        }
    }
}
