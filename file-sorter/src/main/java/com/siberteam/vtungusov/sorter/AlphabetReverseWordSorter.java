package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.stream.Stream;

public class AlphabetReverseWordSorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<SortedPair> pairStream) {
        return pairStream
                .distinct()
                .map(pair -> new SortedPair(pair.getKey(), reverseWord(pair.getValue())))
                .sorted()
                .map(SortedPair::toString);
    }

    private static String reverseWord(String word) {
        return new StringBuilder(word).reverse().toString();
    }
}
