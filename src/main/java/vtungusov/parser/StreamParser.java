package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

public class StreamParser implements Parser<Stream<String>> {
    private static final int HISTOGRAM_SECTION_SIZE = 100;
    private static final String REPORT_TEMPLATE = "%s (%5.2f): %s";
    public static final String HISTOGRAM_UNIT = "#";
    public static final int ACCURACY = 2;
    public static final int PERCENT_LIMIT = 100;
    public static final int HISTOGRAM_MIN_COUNT = 1;

    @Override
    public Report getSymbolFrequencyReport(Stream<String> stringStream, Integer topLineCount) {
        Map<Character, Integer> frequency = getCharFrequency(stringStream);
        int lineCount = (topLineCount == null) ? frequency.size() : topLineCount;
        List<String> report = getReportList(frequency, lineCount);
        return new FrequencyReport(report);
    }

    private List<String> getReportList(Map<Character, Integer> frequencyMap, int lineCount) {
        List<String> report = new ArrayList<>();
        int histVertex = Collections.max(frequencyMap.values());
        float histStep = (float) histVertex / HISTOGRAM_SECTION_SIZE;

        int totalSymbols = frequencyMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(lineCount)
                .forEach(entry -> formReport(report, frequencyMap, totalSymbols, histStep, entry)
                );
        return report;
    }

    private void formReport(List<String> report, Map<Character, Integer> frequencyMap, int totalAmount, float histogramStep, Map.Entry<Character, Integer> mapEntry) {
        BigDecimal percent = BigDecimal.valueOf(frequencyMap.get(mapEntry.getKey()))
                .multiply(BigDecimal.valueOf(PERCENT_LIMIT))
                .divide(BigDecimal.valueOf(totalAmount), ACCURACY, RoundingMode.HALF_EVEN);
        String histogram = getHistogram(histogramStep, mapEntry);
        report.add(String.format(REPORT_TEMPLATE, mapEntry.getKey(), percent.floatValue(), histogram));
    }

    private String getHistogram(float histStep, Map.Entry<Character, Integer> mapEntry) {
        float stepCount;
        if (histStep != 0) {
            stepCount = mapEntry.getValue() / histStep;
        } else {
            stepCount = 0;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stepCount; i++) {
            sb.append(HISTOGRAM_UNIT);
        }

        addGraphToRareSymbol(sb);
        return sb.toString();
    }

    private void addGraphToRareSymbol(StringBuilder sb) {
        if (sb.length() < HISTOGRAM_MIN_COUNT) {
            sb.append(HISTOGRAM_UNIT);
        }
    }

    private Map<Character, Integer> getCharFrequency(Stream<String> stringStream) {
        Map<Character, Integer> frequency = new HashMap<>();
        stringStream
                .flatMapToInt(CharSequence::chars)
                .filter(i -> (!Character.isSpaceChar(i)))
                .forEach(charI -> frequency.compute((char) charI, (k, v) -> (v == null) ? 1 : v + 1));

        return frequency;
    }
}
