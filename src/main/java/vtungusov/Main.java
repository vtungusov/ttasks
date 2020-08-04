package vtungusov;

import vtungusov.parser.StreamParser;
import vtungusov.ui.BadArgumentsException;
import vtungusov.ui.UIManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static vtungusov.util.FileUtil.checkInputFile;
import static vtungusov.util.FileUtil.checkOutputFile;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report" +
            "\n-----------------------------";
    public static final String FILE_READING_ERROR = "Something wrong, file reading error";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        String inputFileName;
        String outputFileName;
        Optional<Integer> lineCount;

        try {
            uiManager.handleOptions(args);
            inputFileName = uiManager.getInputFileName();
            outputFileName = uiManager.getOutputFileName();
            lineCount = uiManager.getTopLineCount();

            checkInputFile(inputFileName);
            checkOutputFile(outputFileName);
        } catch (BadArgumentsException e) {
            return;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            StreamParser streamParser = new StreamParser();
            Stream<String> stringStream = Files.lines(Paths.get(inputFileName));

            streamParser
                    .getSymbolFrequencyReport(stringStream, lineCount.orElse(null))
                    .printToFile(outputFileName);

            System.out.println(SUCCESSFULLY_FINISHED);
        } catch (IOException e) {
            System.out.println(FILE_READING_ERROR);
        }
    }
}
