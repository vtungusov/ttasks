package com.siberteam.vtungusov.vocabulary.fileworker;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWorker {
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final int TARGET_WORD_LENGTH = 3;
    public static final String BAD_URL = "Malformed URL: ";
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";

    private final Set<String> vocabulary = Collections.synchronizedSet(new TreeSet<>());

    public void createVocabulary(Order order) throws IOException {
        validateFiles(order);
        Files.lines(Paths.get(order.getInputFileName()))
                .filter(validateURL())
                .map(convertToURL())
                .parallel()
                .forEach(collectWord());
        saveToFile(order.getOutputFileName(), vocabulary.stream());
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
    }

    private Consumer<URL> collectWord() {
        return url -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                Set<String> wordSet = reader.lines()
                        .map(s -> s.split(STRING_SPLIT_REGEX))
                        .flatMap(Arrays::stream)
                        .filter(byLength())
                        .filter(notNumber())
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                vocabulary.addAll(wordSet);
            } catch (IOException e) {
                vocabulary.addAll(Collections.emptySet());
                throw new IllegalArgumentException(BAD_URL + url);
            }
        };
    }

    private Function<String, URL> convertToURL() {
        return s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException();
            }
        };
    }

    private Predicate<String> validateURL() {
        return s -> {
            UrlValidator validator = new UrlValidator();
            return validator.isValid(s);
        };
    }

    private Predicate<String> byLength() {
        return s -> (s.length() >= TARGET_WORD_LENGTH);
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }

    private void validateFiles(Order order) throws IOException {
        FileUtil.checkInputFile(order.getInputFileName());
        FileUtil.checkOutputFile(order.getOutputFileName());
    }
}
