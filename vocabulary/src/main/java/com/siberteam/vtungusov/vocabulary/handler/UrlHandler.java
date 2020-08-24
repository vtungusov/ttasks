package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.ThreadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

public class UrlHandler {
    public static final int TARGET_WORD_LENGTH = 3;
    public static final String BAD_URL = "Malformed URL: ";
    private static final String STRING_SPLIT_REGEX = "[[^ЁёА-я]]";

    private final Set<String> vocabulary;

    public UrlHandler(Set<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void collectWords(URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            reader.lines()
                    .map(s -> s.split(STRING_SPLIT_REGEX))
                    .flatMap(Arrays::stream)
                    .filter(byLength())
                    .filter(notNumber())
                    .map(String::toLowerCase)
                    .forEach(vocabulary::add);
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
}
