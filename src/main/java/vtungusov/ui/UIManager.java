package vtungusov.ui;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Optional;

public class UIManager {
    private static final String DEFAULT_REPORT_NAME = "report.txt";
    private static final int DEFAULT_TOP_LINE_COUNT = 10;
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    public static final String HEADER = "options:";
    public static final String TOP_VALUE_LIMIT = "Options 't' must be natural number witch more than 0 and less than " + Integer.MAX_VALUE;
    public static final String INCORRECT_ARGUMENT = "Incorrect argument type for option ";
    public static final String INCORRECT_DESCRIPTION_IN = "Incorrect type description in ";
    public static final char FILENAME = 'f';
    public static final char TOP = 't';
    public static final char OUTPUT = 'o';
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

    private boolean validateTopOpt() {
        boolean isValid = false;
        if (cmd.hasOption(TOP)) {
            String optionValue = cmd.getOptionValue(TOP);
            if (optionValue != null) {
                try {
                    int parseInt = Integer.parseInt(optionValue);
                    isValid = (parseInt > 0) && (parseInt < Integer.MAX_VALUE);
                    if (!isValid) {
                        System.out.println(TOP_VALUE_LIMIT);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(TOP_VALUE_LIMIT);
                }
            } else {
                isValid = true;
            }
        } else {
            isValid = true;
        }
        return isValid;
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
                .forEach(o -> {
                            try {
                                options.addOption(Option.builder(o.shortName)
                                        .longOpt(o.longName)
                                        .hasArg(o.hasArg)
                                        .desc(o.description)
                                        .required(o.required)
                                        .optionalArg(o.optionalArg)
                                        .type(Class.forName(o.argType))
                                        .build());
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace(System.err);
                                System.err.println(INCORRECT_DESCRIPTION_IN + OptionInfo.class);
                            }
                        }
                );
        return options;
    }

    public String getInputFileName() {
        return cmd.getOptionValue(FILENAME);
    }

    public String getOutputFileName() {
        String value = cmd.getOptionValue(OUTPUT);
        return value == null ? DEFAULT_REPORT_NAME : value;
    }

    public Optional<Integer> getTopLineCount() throws BadArgumentsException {
        if (validateTopOpt()) {
            Optional<Integer> result = Optional.empty();
            if (cmd.hasOption(TOP)) {
                String optionValue = cmd.getOptionValue(TOP);
                result = (optionValue == null) ?
                        Optional.of(DEFAULT_TOP_LINE_COUNT) : Optional.of(Integer.parseInt(optionValue));
            }
            return result;
        } else throw new BadArgumentsException();
    }
}
