package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
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
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class TaskManager {
    public static final String INTERRUPT_WITH = "Thread was interrupted during collect words in ";
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final String TIMED_OUT = "Task timed out";
    public static final int TIMEOUT_VALUE = 1;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    public static final int LOAD_FACTOR = 4;
    public static final String THREAD_SUCCESS = "Collected words from ";

    private final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        collectWordsFromULRs(order);
        saveToFile(order.getOutputFileName(), vocabulary.stream());
    }

    private void collectWordsFromULRs(Order order) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * LOAD_FACTOR);
        getSubmittedTasks(order, executor)
                .entrySet()
                .forEach(waitResult());
        executor.shutdown();
    }

    private Map<URL, Future<?>> getSubmittedTasks(Order order, ExecutorService executor) throws IOException {
        Map<URL, Future<?>> futureMap;
        try (Stream<String> stream = Files.lines(Paths.get(order.getInputFileName()))) {
            futureMap = stream
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(getFutures(executor));
        }
        return futureMap;
    }

    private Collector<URL, ?, Map<URL, Future<?>>> getFutures(ExecutorService executor) {
        return Collectors.toMap(url -> url, createAndSubmitTask(executor), (o, o2) -> o2);
    }

    private Consumer<Map.Entry<URL, Future<?>>> waitResult() {
        return entry -> {
            try {
                entry.getValue().get(TIMEOUT_VALUE, TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                throw new ThreadException(INTERRUPT_WITH + entry.getKey());
            } catch (ExecutionException e) {
                throw new ThreadException(e.getMessage());
            } catch (TimeoutException e) {
                throw new ThreadException(TIMED_OUT + entry.getKey());
            }
        };
    }

    private Function<URL, Future<?>> createAndSubmitTask(ExecutorService executor) {
        return url ->
                CompletableFuture.runAsync(() -> new UrlHandler(vocabulary).collectWords(url), executor)
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
