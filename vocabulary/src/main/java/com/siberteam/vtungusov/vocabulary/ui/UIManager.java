package com.siberteam.vtungusov.vocabulary.ui;

import com.siberteam.vtungusov.vocabulary.exception.BadArgumentsException;
import org.apache.commons.cli.*;

import java.util.Arrays;

import static com.siberteam.vtungusov.vocabulary.ui.OptionInfo.FILENAME;
import static com.siberteam.vtungusov.vocabulary.ui.OptionInfo.OUTPUT;

public class UIManager {
    public static final String HELP_HEADER = "options:";
    public static final String INCORRECT_ARGUMENT_TYPE = "Incorrect argument type for option ";
    private static final String DEFAULT_OUTPUT_FILENAME = "vocabulary.txt";
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
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
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setWidth(200);
            helpFormatter.printHelp(HELP_TEMPLATE, HELP_HEADER, options, e.getMessage());
        }
        return result;
    }

    private boolean isCorrectType(Option option) {
        try {
            cmd.getParsedOptionValue(option.getOpt());
        } catch (ParseException e) {
            throw new IllegalArgumentException(INCORRECT_ARGUMENT_TYPE + option.getOpt());
        }
        return true;
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
        return value == null ? DEFAULT_OUTPUT_FILENAME : value;
    }
}
