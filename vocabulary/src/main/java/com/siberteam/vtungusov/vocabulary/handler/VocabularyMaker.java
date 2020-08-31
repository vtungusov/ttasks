package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.model.Environment;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.mqbroker.MqBroker;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class VocabularyMaker {
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted while waiting";
    public static final String ERROR_DURING_COLLECTING = "Some error during vocabulary collecting";

    private static final int QUEUE_CAPACITY = 300;
    private static final String THREAD_SUCCESS = "Collected words from";
    private static final int TIMEOUT_VALUE = 10;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final String TIMED_OUT = "Timeout after " + TIMEOUT_VALUE + " " + TIMEOUT_UNIT + " at";
    private static final int COLLECTORS_AMOUNT = 1;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();
    private final Logger logger = LoggerFactory.getLogger(VocabularyMaker.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1
            , r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });
    private final MqBroker mqBroker = new MqBroker();

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        List<CompletableFuture<Void>> collectorsFutures = runWordsCollectors(order);
        runUrlHandlers(order);
        CompletableFuture.allOf(collectorsFutures.toArray(new CompletableFuture[0]))
                .join();
    }

    private List<URL> getURLs(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            return lines
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(Collectors.toList());
        }
    }

    private List<CompletableFuture<Void>> runWordsCollectors(Order order) {
        Environment environment = new Environment(queue, vocabulary, mqBroker, order.getOutputFileName());
        return IntStream.range(0, VocabularyMaker.COLLECTORS_AMOUNT)
                .mapToObj(n -> CompletableFuture
                        .runAsync(() -> new WordsCollector(environment).collectWords())
                        .exceptionally(throwable -> {
                            logger.error("{} {}", ERROR_DURING_COLLECTING, throwable);
                            return null;
                        }))
                .collect(Collectors.toList());
    }

    public void runUrlHandlers(Order order) throws IOException {
        getURLs(order.getInputFileName())
                .forEach(url -> CompletableFuture
                        .runAsync(() -> new UrlHandler(queue).collectWords(url, mqBroker))
                        .applyToEither(failAfter(), Function.identity())
                        .thenAccept(result -> logger.info("{} {}", THREAD_SUCCESS, url))
                        .exceptionally(throwable -> {
                            logger.error("{} {}", throwable.getCause().getMessage(), url);
                            return null;
                        }));
    }

    private <T> CompletableFuture<T> failAfter() {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException(TIMED_OUT);
            return promise.completeExceptionally(ex);
        }, TIMEOUT_VALUE, TIMEOUT_UNIT);
        return promise;
    }

    private void validateFiles(Order order) throws IOException {
        FileUtil.checkInputFile(order.getInputFileName());
        FileUtil.checkOutputFile(order.getOutputFileName());
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
            boolean valid = validator.isValid(s);
            if (!valid) {
                logger.warn("{} {}", BAD_URL, s);
            }
            return valid;
        };
    }
}
