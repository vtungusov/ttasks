package com.siberteam.vtungusov.ui;

import com.siberteam.vtungusov.sorter.AlphabetReverseWordSorter;
import com.siberteam.vtungusov.sorter.AlphabetSorter;
import com.siberteam.vtungusov.sorter.WordLengthSorter;

public enum SortType {
    ALPHABET(AlphabetSorter.class),
    WORD_LENGTH(WordLengthSorter.class),
    ALPHABET_REVERSE_WORD(AlphabetReverseWordSorter.class),
    MY1(Class.class),
    MY2(Class.class);

    Class<?> className;

    SortType(Class<?> className) {
        this.className = className;
    }
}
