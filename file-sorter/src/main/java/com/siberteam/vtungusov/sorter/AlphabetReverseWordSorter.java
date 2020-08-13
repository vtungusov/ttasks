package com.siberteam.vtungusov.sorter;

public class AlphabetReverseWordSorter extends AbstractSorter<String> {
    private static String reverseWord(String word) {
        return new StringBuilder(word).reverse().toString();
    }

    @Override
    protected String getSortFeature(String s) {
        return reverseWord(s);
    }
}
