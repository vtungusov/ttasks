package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

public class WordLengthSorter extends AbstractSorter<Integer> {

    @Override
    protected PairEntry<Integer> getSortFeature(String s) {
        return new PairEntry<>(s, s.length());
    }
}
