package com.siberteam.vtungusov.ui;

public enum OptionInfo {
    FILENAME("f", "filename", true, "file name for sorting",
            true, false, String.class),
    OUTPUT("o", "output", true, "output filename",
            false, false, String.class),
    SORT_CLASS("c", "class", true, "java class which realise sorting. Format: vtungusov.sorter.AlphabetSorter",
            false, false, Class.class),
    SORT_TYPE("d", "descending", false, "Descend sorting output words (ascend sorting without this options)",
            false, false, String.class),
    MULTI_SORT("m", "multisort", true, "Apply all sorter classes for word sorting. Arg = parallelism level (natural number more than 0)",
            false, false, Number.class);

    String shortName;
    String longName;
    boolean hasArg;
    String description;
    boolean required;
    boolean optionalArg;
    Class<?> argType;

    OptionInfo(String shortName, String longName, boolean hasArg, String description, boolean required, boolean optionalArg, Class<?> argType) {
        this.shortName = shortName;
        this.longName = longName;
        this.hasArg = hasArg;
        this.description = description;
        this.required = required;
        this.optionalArg = optionalArg;
        this.argType = argType;
    }
}
