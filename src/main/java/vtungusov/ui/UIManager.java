package vtungusov.ui;

import org.apache.commons.cli.*;

public class UIManager {
    private static final String DEFAULT_REPORT_NAME = "report.txt";
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    private CommandLine cmd;

    public boolean validateOptions(String[] args) {
        Options options = new Options();
        options.addRequiredOption("f", "filename", true, "file name for parsing");
        options.addOption("o", "output", true, "output report name (default: report.txt)");

        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp(HELP_TEMPLATE, "options:", options, e.getMessage());
            return false;
        }
        return true;
    }

    public String[] handleOptions(String[] args) {
        String[] options = new String[2];
        options[0] = cmd.getOptionValue("f");
        if (cmd.hasOption("o")) {
            options[1] = cmd.getOptionValue("o");
        } else {
            options[1] = DEFAULT_REPORT_NAME;
        }
        return options;
    }
}
