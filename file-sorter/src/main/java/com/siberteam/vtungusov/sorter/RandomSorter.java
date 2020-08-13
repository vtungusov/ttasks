package com.siberteam.vtungusov.sorter;

import java.util.Random;

public class RandomSorter extends AbstractSorter<Double> {
    private Random random;

    public RandomSorter() {
        random = new Random();
    }

    @Override
    public Double getSortFeature(String s) {
        return random.nextDouble() * 10;
    }
}
