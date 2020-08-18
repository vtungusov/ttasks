package com.siberteam.vtungusov.fileworker;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;

import java.lang.reflect.Constructor;
import java.util.Set;

public class SortOrder extends AbstractOrder {
    private final Integer threadCount;
    private final Set<Constructor<? extends Sorter>> constructors;

    public SortOrder(String inputFileName, String outputFileName, Set<Constructor<? extends Sorter>> constructors,
                     Integer threadCount, SortDirection direction) {
        super(inputFileName, outputFileName, direction);
        this.constructors = constructors;
        this.threadCount = threadCount;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public Set<Constructor<? extends Sorter>> getConstructors() {
        return constructors;
    }
}
