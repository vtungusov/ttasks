package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.Comparator;

public abstract class AbstractSorter implements Sorter {
    protected Comparator<SortedPair> getComparator(boolean descSort) {
        return descSort ? Comparator.reverseOrder() : Comparator.naturalOrder();
    }
}
