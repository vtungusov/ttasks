package com.siberteam.vtungusov.vocabulary.broker;

import com.siberteam.vtungusov.vocabulary.exception.HandlingException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class WordsBroker {
    public static final String QUEUE_OVERFLOW = "Words queue overflow. Element was missed. Increase queue capacity or handlers count";
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted";
    private static final int QUEUE_CAPACITY = 300;
    private final BlockingQueue<String> wordQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final BlockingQueue<String> poisonQueue = new LinkedBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 2);
    private final Semaphore mutex = new Semaphore(1);
    private Boolean fileSaved = false;

    public void putWord(String word) {
        if (!wordQueue.offer(word)) {
            throw new HandlingException(QUEUE_OVERFLOW);
        }
    }

    public String readWord() {
        try {
            String result;
            if (!poisonQueue.isEmpty()) {
                result = poisonQueue.take();
            } else {
                result = wordQueue.take();
            }
            return result;
        } catch (InterruptedException e) {
            throw new HandlingException(THREAD_INTERRUPT);
        }
    }

    public void putPoison(String poisonWord) {
        if (!poisonQueue.offer(poisonWord)) {
            throw new HandlingException(QUEUE_OVERFLOW);
        }
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
}
