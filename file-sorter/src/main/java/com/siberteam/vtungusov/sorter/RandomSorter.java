package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Comparator;

public class RandomSorter extends AbstractSorter {
    private SortDirection testDirection;

    public RandomSorter() {
    }

    public RandomSorter(SortDirection testDirection) {
        this.testDirection = testDirection;
    }

    @Override
    protected <T extends PairEntry<?>> Comparator<T> getComparator(SortDirection direction) {
        direction = testDirection == null ? direction : testDirection;
        return super.getComparator(direction);
    }

    @Override
    protected PairEntry<String> getSortFeature(String s) {
        return new PairEntry<>(s, s);
    }
}
