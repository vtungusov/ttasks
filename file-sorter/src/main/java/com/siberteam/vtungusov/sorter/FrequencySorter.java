package com.siberteam.vtungusov.sorter;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrequencySorter implements Sorter {
    @Override
    public Stream<String> sort(Stream<String> wordStream) {
        Map<String, Integer> frequency = wordStream
                .collect(Collectors.toMap(k -> k, v -> 1, Integer::sum));
        return frequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey);
    }
}
