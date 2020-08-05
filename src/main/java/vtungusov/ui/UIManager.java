package vtungusov.ui;

import org.apache.commons.cli.*;

import java.util.Arrays;

import static vtungusov.ui.OptionInfo.*;

public class UIManager {
    private static final String DEFAULT_REPORT_NAME = "report.txt";
    private static final int DEFAULT_TOP_LINE_COUNT = 10;
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    public static final String HEADER = "options:";
    public static final String TOP_VALUE_LIMIT = "Options 't' must be natural number more than 0 and less than " + Integer.MAX_VALUE;
    public static final String INCORRECT_ARGUMENT = "Incorrect argument type for option ";
    private CommandLine cmd;

    public void handleOptions(String[] args) throws BadArgumentsException {
        if (!validateOptions(args)) {
            throw new BadArgumentsException();
        }
    }

    private boolean validateOptions(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        boolean result = false;
        try {
            cmd = parser.parse(options, args);
            result = options.getOptions().stream()
                    .allMatch(this::isCorrectType);
        } catch (ParseException e) {
            new HelpFormatter().printHelp(HELP_TEMPLATE, HEADER, options, e.getMessage());
        }
        return result;
    }

    private boolean isCorrectType(Option option) {
        boolean result = false;
        try {
            cmd.getParsedOptionValue(option.getOpt());
            result = true;
        } catch (ParseException e) {
            System.out.println(INCORRECT_ARGUMENT + option.getOpt());
        }
        return result;
    }

    private Options getOptions() {
        Options options = new Options();
        Arrays.stream(OptionInfo.values())
                .map(this::createOption)
                .forEach(options::addOption);
        return options;
    }

    private Option createOption(OptionInfo o) {
        return Option.builder(o.shortName)
                .longOpt(o.longName)
                .hasArg(o.hasArg)
                .desc(o.description)
                .required(o.required)
                .optionalArg(o.optionalArg)
                .type(o.argType)
                .build();
    }

    public String getInputFileName() {
        return cmd.getOptionValue(FILENAME.shortName);
    }

    public String getOutputFileName() {
        String value = cmd.getOptionValue(OUTPUT.shortName);
        return value == null ? DEFAULT_REPORT_NAME : value;
    }

    public Integer getTopLineCount() throws BadArgumentsException {
        Integer result = null;
        boolean hasOption = cmd.hasOption(TOP.shortName);
        String optionValue = cmd.getOptionValue(TOP.shortName);

        if (hasOption) {
            boolean isValid = validateValue(optionValue);
            if (isValid) {
                result = (optionValue == null) ?
                        DEFAULT_TOP_LINE_COUNT : Integer.parseInt(optionValue);
            }
        }
        return result;
    }

    private boolean validateValue(String optionValue) throws BadArgumentsException {
        try {
            if (optionValue != null) {
                int intValue = Integer.parseInt(optionValue);
                if (intValue < 1) {
                    throw new BadArgumentsException(TOP_VALUE_LIMIT);
                }
            }
        } catch (NumberFormatException e) {
            throw new BadArgumentsException(TOP_VALUE_LIMIT);
        }
        return true;
    }
}
