package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.ui.BadArgumentsException;

import java.util.stream.Stream;

public interface Sorter {
    Stream<String> sort(Stream<String> stringStream, SortDirection direction) throws BadArgumentsException;
}
