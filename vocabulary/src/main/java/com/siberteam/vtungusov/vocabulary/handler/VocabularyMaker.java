package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.broker.WordsBroker;
import com.siberteam.vtungusov.vocabulary.exception.HandlingException;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class VocabularyMaker {
    private static final String THREAD_SUCCESS = "Collected words from";
    private static final int TIMEOUT_VALUE = 1;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final String TIMED_OUT = "Timeout after " + TIMEOUT_VALUE + " " + TIMEOUT_UNIT + " at";
    private static final int COLLECTORS_AMOUNT = 2;
    public static final String POISON_PILL = "666";

    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();
    private final Logger logger = LoggerFactory.getLogger(VocabularyMaker.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1
            , r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });
    private final WordsBroker broker = new WordsBroker();

    public void collectVocabulary(Order order) throws IOException {
        validateFiles(order);
        List<CompletableFuture<Void>> collectors = runWordsCollectors(order);
        runUrlHandlers(order, collectors);
        CompletableFuture.allOf(collectors.toArray(new CompletableFuture[0]))
                .join();
    }

    private Set<URL> getURLs(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            return lines
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(Collectors.toSet());
        }
    }

    private List<CompletableFuture<Void>> runWordsCollectors(Order order) {
        return IntStream.range(0, VocabularyMaker.COLLECTORS_AMOUNT)
                .mapToObj(n -> CompletableFuture
                        .runAsync(() -> new WordsCollector().collectWords(broker, vocabulary, order.getOutputFileName())))
                .collect(Collectors.toList());
    }

    private void runUrlHandlers(Order order, List<CompletableFuture<Void>> collectors) throws IOException {
        CompletableFuture.allOf(getUrlHandlersFutures(order))
                .thenAccept(stopCollectors())
                .exceptionally(throwable -> {
                    ThreadException exception = new ThreadException(throwable.getCause().getMessage());
                    collectors.forEach(future -> future.completeExceptionally(exception));
                    return null;
                });
    }

    private CompletableFuture[] getUrlHandlersFutures(Order order) throws IOException {
        return getURLs(order.getInputFileName()).stream()
                .map(url -> CompletableFuture
                        .runAsync(() -> new UrlHandler().collectWords(url, broker))
                        .applyToEither(failAfter(), Function.identity())
                        .thenAccept(result -> logger.info("{} {}", THREAD_SUCCESS, url)))
                .toArray(CompletableFuture[]::new);
    }

    private Consumer<Void> stopCollectors() {
        return aVoid -> {
            for (int i = 0; i < COLLECTORS_AMOUNT; i++) {
                broker.putPoison(POISON_PILL);
                broker.putWord("Found words:");
            }
        };
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
                throw new HandlingException(BAD_URL + s);
            }
            return true;
        };
    }
}
