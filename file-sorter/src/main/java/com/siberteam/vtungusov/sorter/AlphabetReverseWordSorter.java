package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;

@Description("Reverse original word and sort it alphabetically.")
public class AlphabetReverseWordSorter extends AbstractSorter<String> {
    private String reverseWord(String word) {
        return new StringBuilder(word).reverse().toString();
    }

    @Override
    protected String getSortFeature(String s) {
        return reverseWord(s);
    }

    @Override
    public String getName() {
        return "ALPHABET REVERSE WORD SORTER";
    }
}
