package vtungusov.parser;

import vtungusov.report.Report;

import java.util.stream.Stream;

public interface Parser {

    Report getSymbolFrequencyReport(Stream<String> stringStream);

    Report getSymbolFrequencyReport(Stream<String> stringStream, int lineCount);
}
