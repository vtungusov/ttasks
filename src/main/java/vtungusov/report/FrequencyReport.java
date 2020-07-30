package vtungusov.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FrequencyReport implements Report {
    private final List<String> report;

    public FrequencyReport(List<String> report) {
        this.report = report;
    }

    @Override
    public void printToFile(String fileName) {
        printTopToFile(fileName, report.size());
    }

    public void printTopToFile(String fileName, int topCount) {
        try {
            Files.write(Paths.get(fileName), report.subList(0, topCount));
        } catch (IOException e) {
            System.out.println("Error during report creation");
        }
    }
}
