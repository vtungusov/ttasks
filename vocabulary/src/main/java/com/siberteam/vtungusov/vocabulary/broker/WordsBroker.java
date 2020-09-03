package com.siberteam.vtungusov.vocabulary.broker;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public class WordsBroker {
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted";
    private static final int QUEUE_CAPACITY = 300;

    private final BlockingQueue<String> wordQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final Semaphore mutex = new Semaphore(1);
    private final Phaser phaser = new Phaser();
    private Boolean timeToEnd = false;
    private Boolean fileSaved = false;

    public void putWord(String word) {
        try {
            wordQueue.put(word);
        } catch (InterruptedException e) {
            throw new ThreadException(THREAD_INTERRUPT);
        }
    }

    public String readWord() {
        return wordQueue.poll();
    }

    public void timeToEnd() {
        timeToEnd = true;
    }

    public boolean isFileSaved() {
        return fileSaved;
    }

    public void setFileSaved() {
        fileSaved = true;
    }

    public Semaphore getMutex() {
        return mutex;
    }

    public Phaser getPhaser() {
        return phaser;
    }

    public boolean isTimeToEnd() {
        return timeToEnd;
    }

    public void awaitCollectorsFinish() {
        phaser.awaitAdvance(0);
    }

    public boolean trySave() {
        return mutex.tryAcquire();
    }

    public void registerCollector() {
        phaser.register();
    }

    public void unregisterCollector() {
        phaser.arriveAndDeregister();
    }
}
