package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;

import java.util.Random;
import java.util.stream.Stream;

@Description("Assigns each word a random numerical value and sorts by it.")
public class RandomSorter extends AbstractSorter<Double> {
    private final Random random;

    public RandomSorter() {
        random = new Random();
    }

    public RandomSorter(Random random) {
        this.random = random;
    }

    @Override
    public Stream<String> sort(Stream<String> stringStream, SortDirection direction) {
        return stringStream
                .distinct()
                .map(super::getPairEntry)
                .sorted(getComparator(direction))
                .map(this::toString);
    }

    @Override
    public Double getSortFeature(String s) {
        return random.nextDouble() * 10;
    }

    @Override
    public String getName() {
        return "RANDOM WORD SORTER";
    }
}
