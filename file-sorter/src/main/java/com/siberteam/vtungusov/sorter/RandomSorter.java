package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;

import java.util.Random;

public class RandomSorter extends AbstractSorter {
    private static Random random;
    private boolean mockRandom;

    public RandomSorter() {
        random = new Random();
    }

    public RandomSorter(boolean mockRandom) {
        this.mockRandom = mockRandom;
    }

    @Override
    protected PairEntry<Double> getSortFeature(String s) {
        if (mockRandom) {
            return new PairEntry<>(s, 0D);
        } else {
            return new PairEntry<>(s, random.nextDouble() * 10);
        }

    }
}
