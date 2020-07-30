package vtungusov.parser;

import vtungusov.report.Report;

import java.io.IOException;
import java.util.stream.Stream;

public interface Parser {
    Report getSymbolFrequencyReport(Stream<String> stringStream) throws IOException;
}
