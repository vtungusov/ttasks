package com.siberteam.vtungusov.vocabulary.mqbroker;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import com.siberteam.vtungusov.vocabulary.handler.VocabularyMaker;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MqBroker {
    private final Lock lock = new ReentrantLock();
    private final Condition emptyLock = lock.newCondition();
    private final Condition fullLock = lock.newCondition();
    private Boolean isEmpty = true;
    private Boolean isFull = false;

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
