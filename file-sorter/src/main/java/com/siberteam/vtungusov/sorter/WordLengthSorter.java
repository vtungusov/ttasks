package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.comparator.AcsWordLengthComparator;
import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.Comparator;
import java.util.stream.Stream;

public class WordLengthSorter implements Sorter {
    private final Comparator<SortedPair> basicComparator = new AcsWordLengthComparator();

    @Override
    public Stream<String> sort(Stream<SortedPair> pairStream) {
        return pairStream
                .distinct()
                .sorted(basicComparator.thenComparing(Comparator.naturalOrder()))
                .map(SortedPair::toString);
    }
}
