package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class UrlHandler {
    public static final int TARGET_WORD_LENGTH = 3;
    public static final String BAD_URL = "Malformed URL: ";
    public static final String QUEUE_OVERFLOW = "Queue overflow, missing element. Increase queue capacity or handler algorithm";
    private static final String STRING_SPLIT_REGEX = "[[^ЁёА-я]]";
    private final Logger log = LoggerFactory.getLogger(UrlHandler.class);
    private final BlockingQueue<String> queue;

    public UrlHandler(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void collectWords(URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            reader.lines()
                    .map(s -> s.split(STRING_SPLIT_REGEX))
                    .flatMap(Arrays::stream)
                    .filter(byLength())
                    .filter(notNumber())
                    .map(String::toLowerCase)
                    .forEach(this::moveToQueue);
        } catch (IOException e) {
            throw new ThreadException(BAD_URL + url);
        }
    }

    private Predicate<String> byLength() {
        return s -> (s.length() >= TARGET_WORD_LENGTH);
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }

    private void moveToQueue(String e) {
        try {
            queue.offer(e, 10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            log.error(QUEUE_OVERFLOW);
        }
    }
}
