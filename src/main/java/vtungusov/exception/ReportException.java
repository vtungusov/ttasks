package vtungusov.exception;

public class ReportException extends RuntimeException {
    public ReportException() {
        super("Error during report creation");
    }
}
