package vtungusov.ui;

public enum SortType {
    ALPHABET(Class.class),
    WORD_LENGTH(Class.class),
    ALPHABET_REVERSE_WORD(Class.class),
    MY1(Class.class),
    MY2(Class.class);

    Class<?> className;

    SortType(Class<?> className) {
        this.className = className;
    }
}
