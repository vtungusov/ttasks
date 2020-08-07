package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.stream.Stream;

public class AlphabetSorter extends AbstractSorter {
    @Override
    public Stream<String> sort(Stream<SortedPair> pairStream, boolean descSort) {
        return pairStream
                .distinct()
                .sorted(getComparator(descSort))
                .map(SortedPair::toString);
    }
}
