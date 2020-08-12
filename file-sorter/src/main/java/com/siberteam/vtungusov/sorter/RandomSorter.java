package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.PairEntry;
import com.siberteam.vtungusov.ui.BadArgumentsException;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class RandomSorter extends AbstractSorter {
    private Sorter sorter;

    public RandomSorter() {
    }

    public RandomSorter(Sorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public Stream<String> sort(Stream<String> stringStream, SortDirection direction) throws BadArgumentsException {
        Sorter sorter = getSorter();
        return sorter.sort(stringStream, direction);
    }

    private Sorter getSorter() throws BadArgumentsException {
        if (sorter == null) {
            SorterFactory factory = new SorterFactory();
            Set<Class<? extends AbstractSorter>> types = factory.getSorters();
            long sorterNumber = new Random().nextInt(types.size());
            Class<? extends AbstractSorter> sorterClass = types.stream()
                    .skip(sorterNumber)
                    .findFirst()
                    .orElse(AlphabetSorter.class);
            sorter = factory.createSorter(sorterClass);
        }
        return sorter;
    }

    @Override
    protected PairEntry<? extends Comparable<?>> getSortFeature(String s) {
        return null;
    }
}
