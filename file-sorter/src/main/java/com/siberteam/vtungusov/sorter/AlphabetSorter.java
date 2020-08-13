package com.siberteam.vtungusov.sorter;

public class AlphabetSorter extends AbstractSorter<String> {
    @Override
    protected String getSortFeature(String s) {
        return s;
    }
}
