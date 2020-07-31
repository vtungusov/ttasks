package vtungusov.ui;

import org.apache.commons.cli.*;

public class UIManager {
    private static final String DEFAULT_REPORT_NAME = "report.txt";
    private static final int DEFAULT_TOP_COUNT = 10;
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    private CommandLine cmd;

    public boolean validateOptions(String[] args) {
        Options options = new Options();
        options.addRequiredOption("f", "filename", true, "file name for parsing");
        options.addOption("o", "output", true, "output report name");
        options.addOption("t", "top", true, "write only top N report lines (default: 10)");
        Option top = options.getOption("t");
        top.setType(Number.class);
        top.setOptionalArg(true);

        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(options, args);
            cmd.getParsedOptionValue("t");
        } catch (ParseException e) {
            new HelpFormatter().printHelp(HELP_TEMPLATE, "options:", options, e.getMessage());
            return false;
        }
        return true;
    }

    public String[] handleOptions() {
        String[] options = new String[3];

        options[0] = cmd.getOptionValue("f");
        if (cmd.hasOption("o")) {
            options[1] = cmd.getOptionValue("o");
        } else {
            options[1] = DEFAULT_REPORT_NAME;
        }

        if (cmd.hasOption("t")) {
            try {
                if (cmd.getOptionValue("t") == null) {
                    options[2] = String.valueOf(DEFAULT_TOP_COUNT);
                } else {
                    options[2] = String.valueOf(cmd.getParsedOptionValue("t"));
                }
            } catch (ParseException ignore) {
            }
        }
        return options;
    }
}
