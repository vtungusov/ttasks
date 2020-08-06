package vtungusov.ui;

public enum SortType {
    ALPHABET(vtungusov.sorter.AlphabetSorter.class),
    WORD_LENGTH(vtungusov.sorter.WordLengthSorter.class),
    ALPHABET_REVERSE_WORD(vtungusov.sorter.AlphabetReverseWordSorter.class),
    MY1(Class.class),
    MY2(Class.class);

    Class<?> className;

    SortType(Class<?> className) {
        this.className = className;
    }
}
