package com.siberteam.vtungusov.ui;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import org.apache.commons.cli.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static com.siberteam.vtungusov.ui.OptionInfo.*;

public class UIManager {
    private static final String DEFAULT_SORTED_FILENAME = "sorted.txt";
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    public static final String HEADER = "options:";
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
        return value == null ? DEFAULT_SORTED_FILENAME : value;
    }

    public Constructor<? extends Sorter> getSorterClass() throws BadArgumentsException {
        try {
            String optionValue = cmd.getOptionValue(SORT_CLASS.shortName);
            Class<?> sorterClass = Class.forName(optionValue);
            return new SorterFactory()
                    .validateSorter(sorterClass);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new BadArgumentsException(INCORRECT_ARGUMENT + SORT_CLASS.shortName);
        }
    }

    public SortDirection getSortType() {
        return cmd.hasOption(SORT_TYPE.shortName) ?
                SortDirection.DESC : SortDirection.ASC;
    }
}
