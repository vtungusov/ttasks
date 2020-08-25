package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.util.FileUtil;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class TaskManager {
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final String THREAD_SUCCESS = "Collected words from ";
    public static final int QUEUE_CAPACITY = 300;

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        collectWordsFromULRs(order);
        saveToFile(order.getOutputFileName(), vocabulary.stream());
    }

    private void collectWordsFromULRs(Order order) throws IOException {
        Map<URL, CompletableFuture<?>> futures = getSubmittedTasks(order);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]));
        while (!allOf.isDone()) {
            queue.drainTo(vocabulary);
        }
        queue.drainTo(vocabulary);
    }

    private Map<URL, CompletableFuture<?>> getSubmittedTasks(Order order) throws IOException {
        Map<URL, CompletableFuture<?>> futureMap;
        try (Stream<String> stream = Files.lines(Paths.get(order.getInputFileName()))) {
            futureMap = stream
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(getFutures());
        }
        return futureMap;
    }

    private Collector<URL, ?, Map<URL, CompletableFuture<?>>> getFutures() {
        return Collectors.toMap(url -> url, createAndSubmitTask(), (o, o2) -> o2);
    }

    private Function<URL, CompletableFuture<?>> createAndSubmitTask() {
        return url ->
                CompletableFuture.runAsync(() -> new UrlHandler(queue).collectWords(url))
                        .thenAccept(result -> logger.info(THREAD_SUCCESS + url));
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
                throw new IllegalArgumentException(BAD_URL + s);
            }
        };
    }

    private Predicate<String> validateString() {
        return s -> {
            UrlValidator validator = new UrlValidator();
            return validator.isValid(s);
        };
    }

    private void validateFiles(Order order) throws IOException {
        FileUtil.checkInputFile(order.getInputFileName());
        FileUtil.checkOutputFile(order.getOutputFileName());
    }
}
