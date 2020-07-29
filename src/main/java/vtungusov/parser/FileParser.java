package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileParser implements Parser {
    private static final int HISTOGRAM_SECTION_SIZE = 5;
    private final String fileName;

    public FileParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Report getSymbolFrequencyReport() throws IOException {
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
                            float percent = frequencyMap.get(x.getKey()) * 100 / (float) totalAmount;
                            String histogram = getHistogram(histStep, x);
                            report.add(String.format("%s (%5.2f): %s", x.getKey(), percent, histogram));
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

    private Map<String, Integer> getCharFrequency() throws IOException {
        Map<String, Integer> frequency = new HashMap<>();
        Files.lines(Paths.get(fileName))
                .map(String::toCharArray)
                .forEach(m -> {
                    for (char c : m) {
                        if (!Character.isSpaceChar(c)) {
                            frequency.put(String.valueOf(c), frequency.getOrDefault(String.valueOf(c), 1) + 1);
                        }
                    }
                });
        return frequency;
    }
}
