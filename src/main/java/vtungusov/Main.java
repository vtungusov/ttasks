package vtungusov;

import vtungusov.parser.FileParser;
import vtungusov.report.Report;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1 && args[0] != null && args[1] != null) {
            String inputFileName = args[0];
            String outputFileName = args[1];
            FileParser fileParser = new FileParser(inputFileName);
            Report symbolFrequencyReport = fileParser.getSymbolFrequencyReport();

            symbolFrequencyReport.printToFile(outputFileName);
        } else
            System.out.println("Provide input and output file names in arguments (first - input, second - output)");
    }
}
