package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

public class AlphabetSorter extends AbstractSorter {
    @Override
    protected PairEntry<String> getSortFeature(String s) {
        return new PairEntry<>(s, s);
    }
}
