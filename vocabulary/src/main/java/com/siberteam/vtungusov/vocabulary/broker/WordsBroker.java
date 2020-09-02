package com.siberteam.vtungusov.vocabulary.broker;

import com.siberteam.vtungusov.vocabulary.exception.HandlingException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class WordsBroker {
    public static final String QUEUE_OVERFLOW = "Words queue overflow. Element was missed. Increase queue capacity or handlers count";
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted";
    public static final String OFFERING_INTERRUPTED = "Offering word for queue was interrupted";
    private static final int QUEUE_CAPACITY = 300;
    private static final int TIMEOUT = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final Semaphore mutex = new Semaphore(1);
    private Boolean fileSaved = false;

    public void putWord(String word) {
        try {
            if (!queue.offer(word, TIMEOUT, TIME_UNIT)) {
                throw new HandlingException(QUEUE_OVERFLOW);
            }
        } catch (InterruptedException e) {
            throw new HandlingException(OFFERING_INTERRUPTED);
        }
    }

    public String readWord() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new HandlingException(THREAD_INTERRUPT);
        }
    }

    public boolean isFileSaved() {
        synchronized (this) {
            return fileSaved;
        }
    }

    public synchronized void setFileSaved() {
        fileSaved = true;
    }

    public Semaphore getMutex() {
        return mutex;
    }
}
