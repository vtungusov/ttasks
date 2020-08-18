package com.siberteam.vtungusov.fileworker;

import com.siberteam.vtungusov.sorter.SortDirection;

public abstract class AbstractOrder {
    private final String inputFileName;
    private final String outputFileName;
    private final SortDirection direction;

    public AbstractOrder(String inputFileName,
                         String outputFileName,
                         SortDirection direction) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.direction = direction;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public SortDirection getDirection() {
        return direction;
    }
}
