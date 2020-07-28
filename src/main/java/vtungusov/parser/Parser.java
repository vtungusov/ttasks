package vtungusov.parser;

import vtungusov.report.Report;

import java.io.IOException;

public interface Parser {
    Report getSymbolFrequencyReport() throws IOException;
}
