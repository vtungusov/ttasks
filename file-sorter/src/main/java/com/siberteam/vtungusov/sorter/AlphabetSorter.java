package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.stream.Stream;

public class AlphabetSorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<SortedPair> pairStream) {
        return pairStream
                .distinct()
                .sorted()
                .map(SortedPair::toString);
    }
}
