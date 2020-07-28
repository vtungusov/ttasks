package vtungusov.report;

import java.util.List;

public class FrequencyReport implements Report {
    private final List<String> report;

    public FrequencyReport(List<String> report) {
        this.report = report;
    }

    @Override
    public void printToConsole() {
        for (String s : report) {
            System.out.print(s);
        }
    }
}
