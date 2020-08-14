package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;

@Description("Calculate length of original word and sort by it.")
public class WordLengthSorter extends AbstractSorter<Integer> {

    @Override
    protected Integer getSortFeature(String s) {
        return s.length();
    }

    @Override
    public String getName() {
        return "WORD LENGTH SORTER";
    }
}
