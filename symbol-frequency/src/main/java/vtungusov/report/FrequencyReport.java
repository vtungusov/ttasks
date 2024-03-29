package vtungusov.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FrequencyReport {
    private final List<String> report;

    public FrequencyReport(List<String> report) {
        this.report = report;
    }

    public void printToFile(String fileName) {
        try {
            Files.write(Paths.get(fileName), report);
        } catch (IOException e) {
            System.out.println("Error during report write");
        }
    }
}
