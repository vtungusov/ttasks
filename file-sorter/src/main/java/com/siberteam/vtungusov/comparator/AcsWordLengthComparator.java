package com.siberteam.vtungusov.comparator;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.Comparator;

public class AcsWordLengthComparator implements Comparator<SortedPair> {
    @Override
    public int compare(SortedPair o1, SortedPair o2) {
        return o1.getValue().length() - o2.getValue().length();
    }
}
