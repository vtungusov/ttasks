package vtungusov;

import vtungusov.parser.FileParser;
import vtungusov.ui.UIManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report\n-----------------------------";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        if (uiManager.validateOptions(args)) {
            String[] handleArguments = uiManager.handleOptions(args);

            String inputFileName = handleArguments[0];
            String outputFileName = handleArguments[1];

            try {
                new FileParser(Files.lines(Paths.get(inputFileName)))
                        .getSymbolFrequencyReport()
                        .printToFile(outputFileName);
                System.out.println(SUCCESSFULLY_FINISHED);
            } catch (IOException e) {
                System.out.println("File reading error");
            }
        }
    }
}
