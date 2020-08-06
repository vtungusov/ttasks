package com.siberteam.vtungusov.sorter;

import java.util.stream.Stream;

public class AlphabetReverseWordSorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<String> wordStream) {
        return wordStream
                .distinct()
                .map(AlphabetReverseWordSorter::reverseWord)
                .sorted();
    }

    private static String reverseWord(String word) {
        return new StringBuilder(word).reverse().toString();
    }
}
