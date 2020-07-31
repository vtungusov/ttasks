package vtungusov.ui;

import org.apache.commons.cli.*;

public class UIManager {
    private static final String DEFAULT_REPORT_NAME = "report.txt";
    private static final int DEFAULT_TOP_COUNT = 10;
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    private CommandLine cmd;

    public boolean validateOptions(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        boolean result = false;
        try {
            cmd = parser.parse(options, args);
            if (isCorrectType()) {
                result = true;
            }
        } catch (ParseException e) {
            new HelpFormatter().printHelp(HELP_TEMPLATE, "options:", options, e.getMessage());
        }
        return result;
    }

    private Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("f", "filename", true, "file name for parsing");
        options.addOption("o", "output", true, "output report name");
        options.addOption("t", "top", true, "write only top N report lines (default: 10)");
        Option top = options.getOption("t");
        top.setType(Number.class);
        top.setOptionalArg(true);
        return options;
    }

    private boolean isCorrectType() {
        boolean result = false;
        try {
            cmd.getParsedOptionValue("t");
            result = true;
        } catch (ParseException e) {
            System.out.println("Incorrect argument type for option -t");
        }
        return result;
    }

    public String[] handleOptions() {
        String[] options = new String[3];

        getFilenameOpt(options);
        getOutputOpt(options);
        getTopOpt(options);

        return options;
    }

    private void getFilenameOpt(String[] options) {
        options[0] = cmd.getOptionValue("f");
    }

    private void getOutputOpt(String[] options) {
        if (cmd.hasOption("o")) {
            options[1] = cmd.getOptionValue("o");
        } else {
            options[1] = DEFAULT_REPORT_NAME;
        }
    }

    private void getTopOpt(String[] options) {
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
    }
}
