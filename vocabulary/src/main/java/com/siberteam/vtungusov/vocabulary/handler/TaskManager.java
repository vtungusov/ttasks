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
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class TaskManager {
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final String THREAD_SUCCESS = "Collected words from ";
    public static final int QUEUE_CAPACITY = 300;
    public static final String TIMED_OUT = "Timeout after ";
    private static final int TIMEOUT_VALUE = 1;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1
            , r -> {
                Thread thread = new Thread();
                thread.setDaemon(true);
                return thread;
            });

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        collectWordsFromULRs(order);
        saveToFile(order.getOutputFileName(), vocabulary.stream());
    }

    private void collectWordsFromULRs(Order order) throws IOException {
        List<CompletableFuture<?>> futures = getSubmittedTasks(order);
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFuture.thenApply(aVoid -> queue.drainTo(vocabulary));
        while (!allOfFuture.isDone()) {
            queue.drainTo(vocabulary);
        }
        queue.drainTo(vocabulary);
    }

    private List<CompletableFuture<?>> getSubmittedTasks(Order order) throws IOException {
        List<CompletableFuture<?>> futureMap;
        try (Stream<String> stream = Files.lines(Paths.get(order.getInputFileName()))) {
            futureMap = stream
                    .filter(validateString())
                    .map(convertToURL())
                    .map(createAndSubmitTask())
                    .collect(Collectors.toList());
        }
        return futureMap;
    }

    private Function<URL, CompletableFuture<?>> createAndSubmitTask() {
        return url ->
        {
            CompletableFuture<Void> future = addTimeOut(CompletableFuture.supplyAsync(() -> {
                new UrlHandler(queue).collectWords(url);
                return null;
            }));
            return future
                    .thenAccept(result -> logger.info(THREAD_SUCCESS + url))
                    .exceptionally(throwable -> {
                        logger.error(throwable.getMessage(), throwable);
                        return null;
                    });
        };
    }

    private <T> CompletableFuture<T> failAfter() {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException(TIMED_OUT + TIMEOUT_VALUE + " " + TIMEOUT_UNIT);
            return promise.completeExceptionally(ex);
        }, TIMEOUT_VALUE, TIMEOUT_UNIT);
        return promise;
    }

    private <T> CompletableFuture<T> addTimeOut(CompletableFuture<T> future) {
        final CompletableFuture<T> timeout = failAfter();
        return future.applyToEither(timeout, Function.identity());
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
