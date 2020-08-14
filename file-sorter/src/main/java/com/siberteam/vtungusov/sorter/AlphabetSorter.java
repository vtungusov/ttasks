package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;

@Description("Sort original words alphabetically.")
public class AlphabetSorter extends AbstractSorter<String> {
    @Override
    protected String getSortFeature(String s) {
        return s;
    }

    @Override
    public String getName() {
        return "ALPHABET WORD SORTER";
    }
}
