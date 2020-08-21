package com.siberteam.vtungusov.vocabulary.fileworker;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWorker {
    public static final String INTERRUPT_WITH = "Thread was interrupted due collected words ";
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final int TARGET_WORD_LENGTH = 3;
    public static final String BAD_URL = "Malformed URL: ";
    public static final String TIMED_OUT = "Task timed out";
    public static final int TIMEOUT_VALUE = 1;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    public static final int LOAD_FACTOR = 4;
    public static final String THREAD_ERROR = "Error due thread execution with ";

    private final Set<String> vocabulary = Collections.synchronizedSet(new TreeSet<>());

    public void createVocabulary(Order order) throws IOException {
        validateFiles(order);
        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * LOAD_FACTOR, Executors.defaultThreadFactory());
        Map<URL, Future<?>> futureMap = Files.lines(Paths.get(order.getInputFileName()))
                .filter(validateString())
                .map(convertToURL())
                .parallel()
                .collect(getSubmittedTasks(executor));
        futureMap.entrySet().parallelStream()
                .forEach(waitResult());
        executor.shutdown();
        saveToFile(order.getOutputFileName(), vocabulary.stream());
    }

    private Collector<URL, ?, Map<URL, Future<?>>> getSubmittedTasks(ExecutorService executor) {
        return Collectors.toMap(url -> url, createAndSubmitTask(executor), (o, o2) -> o2);
    }

    private Consumer<Map.Entry<URL, Future<?>>> waitResult() {
        return entry -> {
            try {
                entry.getValue().get(TIMEOUT_VALUE, TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                throw new ThreadException(INTERRUPT_WITH + entry.getKey());
            } catch (ExecutionException e) {
                throw new ThreadException(THREAD_ERROR + entry.getKey());
            } catch (TimeoutException e) {
                throw new ThreadException(TIMED_OUT + entry.getKey());
            }
        };
    }

    private Function<URL, ? extends Future<?>> createAndSubmitTask(ExecutorService executor) {
        return url ->
                executor.submit(() -> collectWords(url));
    }

    private void collectWords(URL url) {
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
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
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

    private Predicate<String> validateString() {
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
