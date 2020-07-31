package vtungusov.parser;

import vtungusov.report.Report;

public interface Parser<T> {

    Report getSymbolFrequencyReport(T t);

    Report getSymbolFrequencyReport(T t, int lineCount);
}
