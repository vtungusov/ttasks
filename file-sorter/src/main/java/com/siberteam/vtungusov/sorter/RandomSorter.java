package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;
import com.siberteam.vtungusov.util.SortReflection;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class RandomSorter extends AbstractSorter {
    private static final String BASE_PACKAGE = "com.siberteam.vtungusov";
    private Sorter sorter;

    public RandomSorter() {
    }

    public RandomSorter(Sorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public Stream<String> sort(Stream<String> stringStream, SortDirection direction) {
        Sorter sorter = getSorter();
        return sorter.sort(stringStream, direction);
    }

    private Sorter getSorter() {
        if (sorter == null) {
            Set<Class<? extends AbstractSorter>> types = SortReflection.getSubTypes(AbstractSorter.class);
            long sorterNumber = new Random().nextInt(types.size());
            Class<? extends AbstractSorter> sorterClass = types.stream()
                    .skip(sorterNumber)
                    .findFirst()
                    .orElse(AlphabetSorter.class);
            try {
                sorter = SortReflection.getInstance(sorterClass);
            } catch (InstantiationException | IllegalAccessException ignored) {
            }
        }
        return sorter;
    }

    @Override
    protected PairEntry<? extends Comparable<?>> getSortFeature(String s) {
        return null;
    }
}
