package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class AbstractSorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<String> stringStream, boolean descSort) {
        return stringStream
                .map(this::getSortFeature)
                .distinct()
                .sorted(getComparator(descSort)) //1
                .map(this::toString);
    }

    protected String toString(PairEntry<? extends Comparable<?>> entry) {
        return entry.getKey() + " (" + entry.getValue() + ")";
    }

    protected <T extends PairEntry<?>> Comparator<T> getComparator(boolean descSort) {
        return descSort ? Comparator.reverseOrder() : Comparator.naturalOrder();
    }

    protected abstract PairEntry<? extends Comparable<?>> getSortFeature(String s);
}
