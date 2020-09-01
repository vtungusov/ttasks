package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.broker.WordsBroker;
import com.siberteam.vtungusov.vocabulary.exception.ThreadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Predicate;

public class UrlHandler {
    public static final String BAD_URL = "Malformed URL:";
    private static final int TARGET_WORD_LENGTH = 3;
    private static final String STRING_SPLIT_REGEX = "[[^ЁёА-я]]";

    public void collectWords(URL url, WordsBroker broker) {
        broker.addProducer(this);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            reader.lines()
                    .map(s -> s.split(STRING_SPLIT_REGEX))
                    .flatMap(Arrays::stream)
                    .filter(byLength())
                    .filter(notNumber())
                    .map(String::toLowerCase)
                    .forEach(broker::putWord);
        } catch (IOException e) {
            throw new ThreadException(BAD_URL + url);
        } finally {
            broker.removeProducer(this);
        }
    }

    private Predicate<String> byLength() {
        return s -> (s.length() >= TARGET_WORD_LENGTH);
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }
}
