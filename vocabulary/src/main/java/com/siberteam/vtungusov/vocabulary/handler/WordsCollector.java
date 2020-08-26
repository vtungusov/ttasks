package com.siberteam.vtungusov.vocabulary.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordsCollector {
    public static final String THREAD_SUCCESS = "Collected words from ";
    public static final int QUEUE_CAPACITY = 300;
    private static final int TIMEOUT_VALUE = 1;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    public static final String TIMED_OUT = "Timeout after " + TIMEOUT_VALUE + " " + TIMEOUT_UNIT + " at ";

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final Set<String> vocabulary = new TreeSet<>();
    private final Logger logger = LoggerFactory.getLogger(VocabularyMaker.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1
            , r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });

    public Stream<String> collectWordsFromURLs(List<URL> urlList) {
        List<CompletableFuture<?>> futures = getSubmittedTasks(urlList);
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFuture.thenApply(aVoid -> queue.drainTo(vocabulary));
        while (!allOfFuture.isDone()) {
            queue.drainTo(vocabulary);
        }
        queue.drainTo(vocabulary);
        return vocabulary.stream();
    }

    private List<CompletableFuture<?>> getSubmittedTasks(List<URL> urlList) {
        return urlList.stream()
                .map(createAndSubmitTask())
                .collect(Collectors.toList());
    }

    private Function<URL, CompletableFuture<?>> createAndSubmitTask() {
        return url ->
                CompletableFuture
                        .runAsync(() -> new UrlHandler(queue).collectWords(url))
                        .applyToEither(failAfter(), Function.identity())
                        .thenAccept(result -> logger.info(THREAD_SUCCESS + url))
                        .exceptionally(throwable -> {
                            logger.error(throwable.getCause().getMessage() + url);
                            return null;
                        });
    }

    private <T> CompletableFuture<T> failAfter() {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException(TIMED_OUT);
            return promise.completeExceptionally(ex);
        }, TIMEOUT_VALUE, TIMEOUT_UNIT);
        return promise;
    }
}
