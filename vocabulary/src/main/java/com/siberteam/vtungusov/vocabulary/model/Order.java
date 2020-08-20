package com.siberteam.vtungusov.vocabulary.model;

public class Order {
    private final String inputFileName;
    private final String outputFileName;

    public Order(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }
}
