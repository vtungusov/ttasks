package vtungusov.parser;

import vtungusov.report.Report;

public interface Parser<T> {

    Report getSymbolFrequencyReport(T t, Integer topLineCount);
}
