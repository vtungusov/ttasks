package com.siberteam.vtungusov.vocabulary.model;

import com.siberteam.vtungusov.vocabulary.mqbroker.MqBroker;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Environment {
    private final BlockingQueue<String> queue;
    private final Set<String> vocabulary;
    private final MqBroker mqBroker;
    private final String outputFileName;

    public Environment(BlockingQueue<String> queue, Set<String> vocabulary, MqBroker mqBroker, String outputFileName) {
        this.queue = queue;
        this.vocabulary = vocabulary;
        this.mqBroker = mqBroker;
        this.outputFileName = outputFileName;
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public Set<String> getVocabulary() {
        return vocabulary;
    }

    public MqBroker getMqBroker() {
        return mqBroker;
    }

    public String getOutputFileName() {
        return outputFileName;
    }
}
