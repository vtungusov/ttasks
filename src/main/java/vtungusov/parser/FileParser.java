package vtungusov.parser;

import vtungusov.report.FrequencyReport;
import vtungusov.report.Report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileParser implements Parser {
    private static final int HISTOGRAM_SECTION_SIZE = 5;
    private final File file;

    public FileParser(File file) {
        this.file = file;
    }

    @Override
    public Report getSymbolFrequencyReport() throws IOException {
        Map<String, Integer> map = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String charI;
            for (int i = 0; i < line.length(); i++) {
                charI = line.substring(i, i + 1);
                map.put(charI, map.getOrDefault(charI, 1) + 1);
            }
        }
        reader.close();

        float maxFreq = Collections.max(map.values());

        int histVertex = Math.round(maxFreq);
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

        int totalAmount = 0;
        for (Integer value : map.values()) {
            totalAmount += value;
        }

        List<String> report = new ArrayList<>();
        int finalTotalAmount = totalAmount;
        histogram.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(x -> {
                            float percent = map.get(x.getKey()) * 100 / (float) finalTotalAmount;
                            report.add(String.format("%s (%5.2f): %s \n", x.getKey(), percent, x.getValue()));
                        }
                );
        return new FrequencyReport(report);
    }
}
