package com.siberteam.vtungusov.sorter;

import java.util.stream.Stream;

public interface Sorter {
    Stream<String> sort(Stream<String> stringStream, SortDirection direction);

    String getName();
}
