package com.siberteam.vtungusov.fileworker;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;

import java.lang.reflect.Constructor;

public class ThreadSortOrder extends AbstractOrder {
    private final Constructor<? extends Sorter> constructor;

    public ThreadSortOrder(String inputFileName, String outputFileName, Constructor<? extends Sorter> constructor,
                           SortDirection direction) {
        super(inputFileName, outputFileName, direction);
        this.constructor = constructor;
    }

    public Constructor<? extends Sorter> getConstructor() {
        return constructor;
    }
}
