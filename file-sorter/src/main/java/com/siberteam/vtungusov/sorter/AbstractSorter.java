package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class AbstractSorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<String> stringStream, SortDirection direction) {
        return stringStream
                .map(this::getSortFeature)
                .distinct()
                .sorted(getComparator(direction))
                .map(this::toString);
    }

    protected String toString(PairEntry<? extends Comparable<?>> entry) {
        return entry.getKey() + " (" + entry.getValue() + ")";
    }

    protected <T extends PairEntry<?>> Comparator<T> getComparator(SortDirection direction) {
        Comparator<T> comparator = Comparator.naturalOrder();
        if (direction == SortDirection.DESC) {
            comparator = Comparator.reverseOrder();
        }
        return comparator;
    }

    protected abstract PairEntry<? extends Comparable<?>> getSortFeature(String s);
}
