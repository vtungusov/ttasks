package com.siberteam.vtungusov.sorter;

import java.util.stream.Stream;

public class AlphabetSorter implements Sorter {

    @Override
    public Stream<String> sort(Stream<String> wordStream) {
        return wordStream
                .distinct()
                .sorted();
    }
}
