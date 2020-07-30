package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileParser implements Parser {
    private static final int HISTOGRAM_SECTION_SIZE = 100;
    private static final String REPORT_TEMPLATE = "%s (%5.2f): %s";

    @Override
    public Report getSymbolFrequencyReport(Stream<String> stringStream) {
        Map<String, Integer> frequency = getCharFrequency(stringStream);
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
                .forEach(entry -> formReport(report, frequencyMap, totalAmount, histStep, entry)
                );
        return report;
    }

    private void formReport(List<String> report, Map<String, Integer> frequencyMap, int totalAmount, int histogramStep, Map.Entry<String, Integer> mapEntry) {
        BigDecimal percent = BigDecimal.valueOf(frequencyMap.get(mapEntry.getKey()))
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAmount), 2, RoundingMode.HALF_EVEN);
        String histogram = getHistogram(histogramStep, mapEntry);
        report.add(String.format(REPORT_TEMPLATE, mapEntry.getKey(), percent.floatValue(), histogram));
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
        if (sb.length() < 1) {
            sb.append("#");
        }
        return sb.toString();
    }

    private Map<String, Integer> getCharFrequency(Stream<String> stringStream) {
        Map<String, Integer> frequency = new HashMap<>();
        stringStream
                .map(String::toCharArray)
                .forEach(arr -> setFrequency(frequency, arr));
        return frequency;
    }

    private void setFrequency(Map<String, Integer> frequency, char[] array) {
        for (char chaI : array) {
            if (!Character.isSpaceChar(chaI)) {
                frequency.compute(String.valueOf(chaI), (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
    }

    public static void checkInputFile(String inputFileName) throws IOException {
        Path path = Paths.get(inputFileName);
        if (Files.notExists(path)) {
            throw new FileNotFoundException("File not found");
        }
        if (!Files.isReadable(path)) {
            throw new IOException("Can`t read the file, access denied");
        }
        if (Files.size(path) < 1) {
            throw new IOException("Input file is empty");
        }
    }

    public static void checkOutputFile(String inputFileName) throws IOException {
        Path path = Paths.get(inputFileName);
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new IOException("Can`t create report file, access denied");
            }
        }

        if (!Files.isWritable(path)) {
            throw new IOException("Can`t write to the file, access denied");
        }
    }
}
