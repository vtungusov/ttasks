package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

public class FileParser implements Parser {
    private static final int HISTOGRAM_SECTION_SIZE = 5;
    private static final String REPORT_TEMPLATE = "%s (%5.2f): %s";
    private final Stream<String> stringStream;

    public FileParser(Stream<String> fileInputStream) {
        this.stringStream = fileInputStream;
    }

    @Override
    public Report getSymbolFrequencyReport() {
        Map<String, Integer> frequency = getCharFrequency();
        List<String> report = getReportList(frequency);
        return new FrequencyReport(report);
    }

    private List<String> getReportList(Map<String, Integer> frequencyMap) {
        int totalAmount = frequencyMap.values().stream()
                .mapToInt(Integer::intValue).sum();

        List<String> report = new ArrayList<>();
        int histVertex = Collections.max(frequencyMap.values());
        int histStep = histVertex / HISTOGRAM_SECTION_SIZE;

        frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(x -> {
                            BigDecimal percent = BigDecimal.valueOf(frequencyMap.get(x.getKey()))
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(BigDecimal.valueOf(totalAmount), 2, RoundingMode.HALF_EVEN);
                            String histogram = getHistogram(histStep, x);
                            report.add(String.format(REPORT_TEMPLATE, x.getKey(), percent.floatValue(), histogram));
                        }
                );
        return report;
    }

    private String getHistogram(int histStep, Map.Entry<String, Integer> mapEntry) {
        int stepCount;
        if (histStep != 0) {
            stepCount = mapEntry.getValue() / histStep;
        } else {
            stepCount = 0;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stepCount; i++) {
            sb.append('#');
        }
        return sb.toString();
    }

    private Map<String, Integer> getCharFrequency() {
        Map<String, Integer> frequency = new HashMap<>();
        stringStream
                .map(String::toCharArray)
                .forEach(m -> {
                    for (char c : m) {
                        if (!Character.isSpaceChar(c)) {
                            frequency.compute(String.valueOf(c), (k, v) -> (v == null) ? 1 : v + 1);
                        }
                    }
                });
        return frequency;
    }
}
