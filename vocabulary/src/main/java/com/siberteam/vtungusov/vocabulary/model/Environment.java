package com.siberteam.vtungusov.vocabulary.model;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

public class Environment {
    private final BlockingQueue<String> queue;
    private final Set<String> vocabulary;
    private final CountDownLatch doneSignal;

    public Environment(BlockingQueue<String> queue, Set<String> vocabulary, CountDownLatch doneSignal) {
        this.queue = queue;
        this.vocabulary = vocabulary;
        this.doneSignal = doneSignal;
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public Set<String> getVocabulary() {
        return vocabulary;
    }

    public CountDownLatch getDoneSignal() {
        return doneSignal;
    }
}
