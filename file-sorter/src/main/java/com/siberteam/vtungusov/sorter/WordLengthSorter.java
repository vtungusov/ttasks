package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.comparator.AcsWordLengthComparator;
import com.siberteam.vtungusov.comparator.DescWordLengthComparator;
import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.Comparator;
import java.util.stream.Stream;

public class WordLengthSorter extends AbstractSorter {
    private final Comparator<SortedPair> basicComparator = new AcsWordLengthComparator();

    @Override
    public Stream<String> sort(Stream<SortedPair> pairStream, boolean descSort) {
        return pairStream
                .distinct()
                .sorted(getComparator(descSort).thenComparing(Comparator.naturalOrder()))
                .map(SortedPair::toString);
    }

    @Override
    protected Comparator<SortedPair> getComparator(boolean descSort) {
        return descSort ? new DescWordLengthComparator() : new AcsWordLengthComparator();
    }
}
