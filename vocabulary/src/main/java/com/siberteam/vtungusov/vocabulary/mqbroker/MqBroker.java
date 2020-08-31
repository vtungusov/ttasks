package com.siberteam.vtungusov.vocabulary.mqbroker;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import com.siberteam.vtungusov.vocabulary.handler.UrlHandler;
import com.siberteam.vtungusov.vocabulary.handler.VocabularyMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MqBroker {
    private final List<UrlHandler> producers = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition emptyLock = lock.newCondition();
    private final Condition fullLock = lock.newCondition();
    private Boolean fileSaved = false;
    private Boolean isEmpty = true;
    private Boolean isFull = false;

    public synchronized void addProducer(UrlHandler producer) {
        producers.add(producer);
    }

    public void removeProducer(UrlHandler producer) {
        lock.lock();
        producers.remove(producer);
        synchronized (emptyLock) {
            emptyLock.signalAll();
        }
        lock.unlock();
    }

    public synchronized boolean producersFinished() {
        return producers.isEmpty();
    }

    public void waitSpace() throws InterruptedException {
        lock.lock();
        if (isFull()) {
            fullLock.await();
        }
        lock.unlock();
    }

    public void sayFull() {
        synchronized (fullLock) {
            isFull = true;
        }
    }

    public void sayNotFull() {
        lock.lock();
        isFull = false;
        fullLock.signalAll();
        lock.unlock();
    }

    public void sayEmpty() {
        synchronized (emptyLock) {
            isEmpty = true;
        }
    }

    public void sayNotEmpty() {
        lock.lock();
        isEmpty = false;
        emptyLock.signalAll();
        lock.unlock();
    }

    public void waitData() {
        lock.lock();
        if (isEmpty()) {
            try {
                emptyLock.await();
            } catch (InterruptedException e) {
                throw new ThreadException(VocabularyMaker.THREAD_INTERRUPT);
            }
        }
        lock.unlock();
    }

    public boolean isFileSaved() {
        synchronized (this) {
            return fileSaved;
        }
    }

    public synchronized void sayFileSaved() {
        fileSaved = true;
    }

    private boolean isFull() {
        synchronized (fullLock) {
            return isFull;
        }
    }

    private boolean isEmpty() {
        synchronized (emptyLock) {
            return isEmpty;
        }
    }
}
