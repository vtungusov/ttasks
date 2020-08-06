package com.siberteam.vtungusov.ui;

public enum OptionInfo {
    FILENAME("f", "filename", true, "file name for sorting",
            true, false, String.class),
    OUTPUT("o", "output", true, "output filename",
            false, false, String.class),
    SORT_CLASS("c", "class", true, "java class which realise sorting. Format: vtungusov.sorter.AlphabetSorter",
            true, false, Class.class);

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
