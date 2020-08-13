package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class AbstractSorter<T extends Comparable<T>> implements Sorter {
    @Override
    public Stream<String> sort(Stream<String> stringStream, SortDirection direction) {
        return stringStream
                .map(this::getSortFeature)
                .distinct()
                .sorted(getComparator(direction))
                .map(this::toString);
    }

    protected String toString(PairEntry<T> entry) {
        return entry.getKey() + " (" + entry.getValue() + ")";
    }

    protected <U extends PairEntry<T>> Comparator<U> getComparator(SortDirection direction) {
        Comparator<U> comparator = Comparator.naturalOrder();
        if (direction == SortDirection.DESC) {
            comparator = Comparator.reverseOrder();
        }
        return comparator.thenComparing(Comparator.naturalOrder());
    }

    protected abstract PairEntry<T> getSortFeature(String s);
}
