package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;

import java.util.stream.Stream;

public interface Sorter {
    Stream<String> sort(Stream<SortedPair> pairStream);
}
