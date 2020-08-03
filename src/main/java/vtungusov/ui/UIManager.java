package vtungusov.ui;

import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

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
            result = isCorrectType() && validateTopOpt();
        } catch (ParseException e) {
            new HelpFormatter().printHelp(HELP_TEMPLATE, "options:", options, e.getMessage());
        }
        return result;
    }

    private boolean validateTopOpt() {
        boolean isValid;
        if (cmd.hasOption('t') && isCorrectType()) {
            String optionValue = cmd.getOptionValue('t');
            if (optionValue != null) {
                isValid = Integer.parseInt(optionValue) > 0;
                if (!isValid) {
                    System.out.println("Options 't' must be more than 0");
                }
            } else {
                isValid = true;
            }
        } else {
            isValid = !cmd.hasOption("t");
        }
        return isValid;
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

    public Map<String, String> handleOptions() {
        Map<String, String> options = new HashMap<>();

        getFilenameOpt(options);
        getOutputOpt(options);
        getTopOpt(options);

        return options;
    }

    private void getFilenameOpt(Map<String, String> options) {
        options.put("f", cmd.getOptionValue("f"));
    }

    private void getOutputOpt(Map<String, String> options) {
        options.compute("o", (k, v) -> (cmd.getOptionValue("o") == null) ?
                DEFAULT_REPORT_NAME : cmd.getOptionValue("o"));
    }

    private void getTopOpt(Map<String, String> options) {
        if (cmd.hasOption("t")) {
            options.compute("t", (k, v) -> (cmd.getOptionValue("t") == null) ?
                    String.valueOf(DEFAULT_TOP_COUNT) : cmd.getOptionValue("t"));
        }
    }
}
