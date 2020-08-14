package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;
import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Description("Calculate frequency of original word and sort by it.")
public class FrequencySorter extends AbstractSorter<Integer> {
    @Override
    public Stream<String> sort(Stream<String> wordStream, SortDirection direction) {
        Map<String, Integer> frequency = getFrequency(wordStream);
        return frequency.entrySet().stream()
                .map(e -> new PairEntry<>(e.getKey(), e.getValue()))
                .sorted(this.getComparator(direction).thenComparing(PairEntry::getKey))
                .map(this::toString);
    }

    @Override
    protected Integer getSortFeature(String s) {
        throw new UnsupportedOperationException();
    }

    private Map<String, Integer> getFrequency(Stream<String> wordStream) {
        return wordStream
                .collect(Collectors
                        .toMap(k -> k, v -> 1, Integer::sum));
    }

    @Override
    public String getName() {
        return "FREQUENCY WORD SORTER";
    }
}
