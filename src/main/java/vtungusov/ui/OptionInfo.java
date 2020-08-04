package vtungusov.ui;

public enum OptionInfo {
    FILENAME("f", "filename", true, "file name for parsing",
            true, false, java.lang.String.class),
    OUTPUT("o", "output", true, "output report name",
            false, false, java.lang.String.class),
    TOP("t", "top", true, "write only top N report lines (default: 10)",
            false, true, java.lang.Number.class);

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
