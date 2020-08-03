package vtungusov;

import vtungusov.parser.StreamParser;
import vtungusov.ui.UIManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import static vtungusov.util.FileUtil.checkInputFile;
import static vtungusov.util.FileUtil.checkOutputFile;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report\n-----------------------------";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        if (uiManager.validateOptions(args)) {
            Map<String, String> handleArguments = uiManager.handleOptions();

            String inputFileName = handleArguments.get("f");
            String outputFileName = handleArguments.get("o");
            String lineCount = handleArguments.getOrDefault("t", "0");

            try {
                checkInputFile(inputFileName);
                checkOutputFile(outputFileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }

            try {
                StreamParser streamParser = new StreamParser();
                Stream<String> stringStream = Files.lines(Paths.get(inputFileName));

                streamParser
                        .getSymbolFrequencyReport(stringStream, Integer.parseInt(lineCount))
                        .printToFile(outputFileName);

                System.out.println(SUCCESSFULLY_FINISHED);
            } catch (IOException e) {
                System.out.println("Something wrong, file reading error");
            }
        }
    }
}
