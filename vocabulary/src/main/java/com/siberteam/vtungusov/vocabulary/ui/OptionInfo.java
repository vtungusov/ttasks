package com.siberteam.vtungusov.vocabulary.ui;

public enum OptionInfo {
    FILENAME("f", "filename", true, "Name of file with URL list",
            true, false, String.class),
    OUTPUT("o", "output", true, "Vocabulary output filename",
            false, false, String.class);

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
