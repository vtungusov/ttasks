package com.siberteam.vtungusov.vocabulary.ui;

import com.siberteam.vtungusov.vocabulary.exception.BadArgumentsException;
import org.apache.commons.cli.*;

import java.util.Arrays;

import static com.siberteam.vtungusov.vocabulary.ui.OptionInfo.*;

public class UIManager {
    public static final String HELP_HEADER = "options:";
    public static final String INCORRECT_ARGUMENT_TYPE = "Incorrect argument type for option ";
    public static final int MIN_THREAD_COUNT = 1;
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final String THREAD_COUNT_LIMIT = "Options 'c' must be natural number more than " + MIN_THREAD_COUNT +
            " and less than " + (AVAILABLE_PROCESSORS + 1);
    public static final int DEFAULT_COLLECTORS_AMOUNT = 1;
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

    public int getCollectorsCount() throws BadArgumentsException {
        int threadCount = DEFAULT_COLLECTORS_AMOUNT;
        if (cmd.hasOption(COLLECTORS.shortName)) {
            try {
                threadCount = Integer.parseInt(cmd.getOptionValue(COLLECTORS.shortName));
                if (threadCount < MIN_THREAD_COUNT || threadCount >= AVAILABLE_PROCESSORS) {
                    throw new BadArgumentsException(THREAD_COUNT_LIMIT);
                }
            } catch (NumberFormatException e) {
                throw new BadArgumentsException(THREAD_COUNT_LIMIT);
            }
        }
        return threadCount;
    }
}
