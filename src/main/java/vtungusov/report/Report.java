package vtungusov.report;

public interface Report {
    void printToFile(String fileName);

    void printTopToFile(String fileName, int count);
}
