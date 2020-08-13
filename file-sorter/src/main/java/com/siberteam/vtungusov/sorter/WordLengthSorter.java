package com.siberteam.vtungusov.sorter;

public class WordLengthSorter extends AbstractSorter<Integer> {

    @Override
    protected Integer getSortFeature(String s) {
        return s.length();
    }
}
