package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

public class AlphabetReverseWordSorter extends AbstractSorter {
    private static String reverseWord(String word) {
        return new StringBuilder(word).reverse().toString();
    }

    @Override
    protected PairEntry<String> getSortFeature(String s) {
        return new PairEntry<>(s, reverseWord(s));
    }
}
