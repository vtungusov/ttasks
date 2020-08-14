package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;

import java.util.Random;

@Description("Assigns each word a random numerical value and sorts by it.")
public class RandomSorter extends AbstractSorter<Double> {
    private final Random random;

    public RandomSorter() {
        random = new Random();
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
