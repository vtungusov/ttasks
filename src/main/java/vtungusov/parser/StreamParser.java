package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamParser implements Parser<Stream<String>> {
    private static final int HISTOGRAM_SECTION_SIZE = 100;
    private static final String REPORT_TEMPLATE = "%s (%5.2f): %s";
    private int totalSymbols = 0;

    @Override
    public Report getSymbolFrequencyReport(Stream<String> stringStream) {
        Map<Character, Integer> frequency = getCharFrequency(stringStream);
        List<String> report = getReportList(frequency);
        return new FrequencyReport(report);
    }

    @Override
    public Report getSymbolFrequencyReport(Stream<String> stringStream, int lineCount) {
        Map<Character, Integer> frequency = getCharFrequency(stringStream, lineCount);
        List<String> report = getReportList(frequency);
        return new FrequencyReport(report);
    }

    private List<String> getReportList(Map<Character, Integer> frequencyMap) {
        List<String> report = new ArrayList<>();
        int histVertex = Collections.max(frequencyMap.values());
        float histStep = (float) histVertex / HISTOGRAM_SECTION_SIZE;

        frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> formReport(report, frequencyMap, totalSymbols, histStep, entry)
                );
        return report;
    }

    private void formReport(List<String> report, Map<Character, Integer> frequencyMap, int totalAmount, float histogramStep, Map.Entry<Character, Integer> mapEntry) {
        BigDecimal percent = BigDecimal.valueOf(frequencyMap.get(mapEntry.getKey()))
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAmount), 2, RoundingMode.HALF_EVEN);
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
            sb.append('#');
        }
        if (sb.length() < 1) {
            sb.append("#");
        }
        return sb.toString();
    }

    private Map<Character, Integer> getCharFrequency(Stream<String> stringStream) {
        Map<Character, Integer> frequency = new HashMap<>();
        stringStream
                .forEach(line -> line.chars()
                        .filter(i -> (!Character.isSpaceChar(i)))
                        .forEach(charI -> frequency.compute((char) charI, (k, v) -> (v == null) ? 1 : v + 1)));
        this.totalSymbols = frequency.values().stream().mapToInt(Integer::intValue).sum();
        return frequency;
    }

    private Map<Character, Integer> getCharFrequency(Stream<String> stringStream, int lineCount) {
        Map<Character, Integer> charFrequency = getCharFrequency(stringStream);
        return charFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(lineCount).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
