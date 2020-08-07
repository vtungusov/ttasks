package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrequencySorter extends AbstractSorter {
    @Override
    public Stream<String> sort(Stream<SortedPair> wordStream, boolean descSort) {
        Map<String, Integer> frequency = wordStream
                .collect(Collectors
                        .toMap(Pair::getKey, v -> 1, Integer::sum));
        return frequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(getIntComparator(descSort)))
                .map(this::toString);
    }

    private Comparator<Integer> getIntComparator(boolean descSort) {
        return descSort ? Comparator.reverseOrder() : Comparator.naturalOrder();
    }

    private String toString(Map.Entry<String, Integer> entry) {
        String word = entry.getKey();
        return word + " (" + word + ")";
    }
}
