package com.siberteam.vtungusov.model;

import com.siberteam.vtungusov.sorter.SortDirection;

import java.util.Set;

public class Order {
    private final String inputFileName;
    private final String outputFileName;
    private final Set<SorterData> sortersData;
    private final Integer threadCount;
    private final SortDirection direction;

    public Order(String inputFileName, String outputFileName, Set<SorterData> sortersData,
                 Integer threadCount, SortDirection direction) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.sortersData = sortersData;
        this.threadCount = threadCount;
        this.direction = direction;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public Set<SorterData> getSortersDataSet() {
        return sortersData;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public SortDirection getDirection() {
        return direction;
    }
}
