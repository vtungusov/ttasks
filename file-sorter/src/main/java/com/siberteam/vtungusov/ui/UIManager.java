package com.siberteam.vtungusov.ui;

import com.siberteam.vtungusov.model.SorterData;
import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.SorterFactory;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collector;

import static com.siberteam.vtungusov.ui.OptionInfo.*;

public class UIManager {
    private static final String DEFAULT_OUTPUT_FILENAME = "sorted.txt";
    private static final String HELP_TEMPLATE = "java -jar [jar name] [options]";
    public static final String HELP_HEADER = "options:";
    public static final String INCORRECT_ARGUMENT_TYPE = "Incorrect argument type for option ";
    public static final String SUPPORTABLE_CLASSES_HEADER = "Supportable classes:";
    public static final int MIN_THREAD_COUNT = 2;
    public static final String THREAD_COUNT_LIMIT = "Options 'm' must be natural number more than " + (MIN_THREAD_COUNT - 1) + " and less than " + Integer.MAX_VALUE;
    public static final String MUST_BE_DECLARED = "Options 'c' or 'm' must be declared!";

    private final SorterFactory sorterFactory;
    private CommandLine cmd;

    public UIManager(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void handleOptions(String[] args) throws BadArgumentsException {
        if (!validateOptions(args)) {
            throw new BadArgumentsException();
        }
        if (!cmd.hasOption(SORT_CLASS.shortName) && !cmd.hasOption(MULTI_SORT.shortName)) {
            throw new BadArgumentsException(MUST_BE_DECLARED);
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
            String classes = getSupportableClasses();
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.setWidth(200);
            helpFormatter.printHelp(HELP_TEMPLATE, HELP_HEADER, options, classes + e.getMessage());
        }
        return result;
    }

    private boolean isCorrectType(Option option) {
        try {
            cmd.getParsedOptionValue(option.getOpt());
        } catch (ParseException e) {
            throw new RuntimeException(INCORRECT_ARGUMENT_TYPE + option.getOpt());
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

    private String getSupportableClasses() {
        StringBuilder sorters = sorterFactory.getAllSorterData().stream()
                .map(sData -> sData.getName() + ": " + sData.getDescription())
                .collect(putToString());
        return SUPPORTABLE_CLASSES_HEADER
                + System.lineSeparator()
                + sorters;
    }

    private Collector<String, StringBuilder, StringBuilder> putToString() {
        return Collector.of(
                StringBuilder::new,
                (sb, str) -> sb.append(str).append(System.lineSeparator()),
                StringBuilder::append);
    }

    public String getInputFileName() {
        return cmd.getOptionValue(FILENAME.shortName);
    }

    public String getOutputFileName() {
        String value = cmd.getOptionValue(OUTPUT.shortName);
        return value == null ? DEFAULT_OUTPUT_FILENAME : value;
    }

    public Set<SorterData> getSortersData() throws BadArgumentsException {
        String optionValue = cmd.getOptionValue(SORT_CLASS.shortName);
        if (optionValue != null) {
            return Collections.singleton(sorterFactory.getSorterData(optionValue));
        } else {
            return sorterFactory.getAllSorterData();
        }
    }

    public SortDirection getSortType() {
        return cmd.hasOption(SORT_TYPE.shortName) ?
                SortDirection.DESC : SortDirection.ASC;
    }

    public int getThreadCount() throws BadArgumentsException {
        int threadCount = 1;
        if (cmd.hasOption(MULTI_SORT.shortName)) {
            try {
                threadCount = Integer.parseInt(cmd.getOptionValue(MULTI_SORT.shortName));
                if (threadCount < MIN_THREAD_COUNT) {
                    throw new BadArgumentsException(THREAD_COUNT_LIMIT);
                }
            } catch (NumberFormatException e) {
                throw new BadArgumentsException(THREAD_COUNT_LIMIT);
            }
        }
        return threadCount;
    }
}
