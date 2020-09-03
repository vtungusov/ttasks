package com.siberteam.vtungusov.vocabulary.model;

public class Order {
    private final String inputFileName;
    private final String outputFileName;
    private final int collectorsCount;

    public Order(String inputFileName, String outputFileName, int collectorsCount) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.collectorsCount = collectorsCount;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }
}
