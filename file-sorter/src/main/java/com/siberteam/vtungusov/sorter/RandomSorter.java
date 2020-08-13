package com.siberteam.vtungusov.sorter;

import java.util.Random;

public class RandomSorter extends AbstractSorter<Double> {
    private static Random random;
    private boolean mockRandom;

    public RandomSorter() {
        random = new Random();
    }

    public RandomSorter(boolean mockRandom) {
        this.mockRandom = mockRandom;
    }

    @Override
    protected Double getSortFeature(String s) {
        if (mockRandom) {
            return 0D;
        } else {
            return random.nextDouble() * 10;
        }
    }
}
