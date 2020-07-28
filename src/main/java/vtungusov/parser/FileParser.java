package vtungusov.parser;

import vtungusov.exception.ReportException;
import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileParser implements Parser {
    private static final int HISTOGRAM_SECTION_SIZE = 5;
    private final String fileName;

    public FileParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Report getSymbolFrequencyReport() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Map<String, Integer> map = getCharFrequency(reader);
            Map<String, String> histogram = formHistogram(map);

            List<String> report = getReportList(map, histogram);
            return new FrequencyReport(report);
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
            throw new ReportException();
        } catch (IOException e) {
            System.out.println("File reading error");
            throw new ReportException();
        }
    }

    private List<String> getReportList(Map<String, Integer> map, Map<String, String> histogram) {
        int totalAmount = map.values().stream()
                .mapToInt(Integer::intValue).sum();

        List<String> report = new ArrayList<>();
        histogram.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(x -> {
                            float percent = map.get(x.getKey()) * 100 / (float) totalAmount;
                            report.add(String.format("%s (%5.2f): %s \n", x.getKey(), percent, x.getValue()));
                        }
                );
        return report;
    }

    private Map<String, String> formHistogram(Map<String, Integer> map) {
        int histVertex = Collections.max(map.values());
        int histStep = histVertex / HISTOGRAM_SECTION_SIZE;

        Map<String, String> histogram = new HashMap<>();
        map.forEach((key, value) -> {
            StringBuilder sb = new StringBuilder();
            int stepCount;
            if (histStep != 0) {
                stepCount = value / histStep;
            } else stepCount = 0;

            for (int i = 0; i < stepCount; i++) {
                sb.append('#');
            }
            histogram.put(key, sb.toString());
        });
        return histogram;
    }

    private Map<String, Integer> getCharFrequency(BufferedReader reader) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String charI;
            for (int i = 0; i < line.length(); i++) {
                charI = line.substring(i, i + 1);
                map.put(charI, map.getOrDefault(charI, 1) + 1);
            }
        }
        return map;
    }
}
