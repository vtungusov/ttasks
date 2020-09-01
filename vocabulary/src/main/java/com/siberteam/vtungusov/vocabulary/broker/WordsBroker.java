package com.siberteam.vtungusov.vocabulary.broker;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import com.siberteam.vtungusov.vocabulary.handler.UrlHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WordsBroker {
    public static final String QUEUE_OVERFLOW = "Queue overflow. Element was missed. Increase queue capacity or handlers count";
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted while waiting";
    public static final String OFFERING_INTERRUPTED = "Offering word for queue was interrupted";
    public static final String POISON_PILL = "666";
    private static final int QUEUE_CAPACITY = 300;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int TIMEOUT = 1;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final Logger log = LoggerFactory.getLogger(WordsBroker.class);
    private final List<UrlHandler> producers = new ArrayList<>();
    private Boolean fileSaved = false;

    public WordsBroker() {
    }

    public void putWord(String word) {
        try {
            if (!queue.offer(word, TIMEOUT, TIME_UNIT)) {
                log.debug(QUEUE_OVERFLOW);
            }
        } catch (InterruptedException e) {
            log.error(OFFERING_INTERRUPTED);
        }
    }

    public String readWord() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error(THREAD_INTERRUPT);
            throw new ThreadException();
        }
    }

    public synchronized void addProducer(UrlHandler producer) {
        producers.add(producer);
    }

    public synchronized void removeProducer(UrlHandler producer) {
        producers.remove(producer);
        if (producers.isEmpty()) {
            queue.add(POISON_PILL);
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
}
