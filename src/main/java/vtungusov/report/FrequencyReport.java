package vtungusov.report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FrequencyReport implements Report {
    private final List<String> report;

    public FrequencyReport(List<String> report) {
        this.report = report;
    }

    @Override
    public void printToFile(String fileName) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            for (String s : report) {
                out.write(s);
            }
        } catch (IOException e) {
            System.out.println("Error during report creation");
        }
    }
}
