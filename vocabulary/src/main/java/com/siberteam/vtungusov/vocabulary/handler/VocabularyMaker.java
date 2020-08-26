package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.exception.FileIOException;
import com.siberteam.vtungusov.vocabulary.exception.ThreadException;
import com.siberteam.vtungusov.vocabulary.model.Environment;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.vocabulary.handler.UrlHandler.BAD_URL;

public class VocabularyMaker {
    private static final String WRITE_ERROR = "Error during file writing ";
    private static final int QUEUE_CAPACITY = 300;
    private static final String THREAD_SUCCESS = "Collected words from ";
    public static final String THREAD_INTERRUPT = "Thread execution was interrupted while waiting";
    private static final int TIMEOUT_VALUE = 1;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final String TIMED_OUT = "Timeout after " + TIMEOUT_VALUE + " " + TIMEOUT_UNIT + " at ";
    private static final int COLLECTORS_AMOUNT = 1;
    public static final String ERROR_DURING_COLLECTING = "Some error during vocabulary collecting";

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final Set<String> vocabulary = new ConcurrentSkipListSet<>();
    private final Logger logger = LoggerFactory.getLogger(VocabularyMaker.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1
            , r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });
    private final CountDownLatch consumersDoneSignal = new CountDownLatch(COLLECTORS_AMOUNT);
    private CountDownLatch producersDoneSignal;

    public void collectVocabulary(Order order) throws IOException {
        try {
            validateFiles(order);
            List<URL> urls = initLatchAndGetURLs(order.getInputFileName());
            runWordsCollectors();
            runUrlHandlers(urls);
            consumersDoneSignal.await();
        } catch (InterruptedException e) {
            throw new ThreadException(THREAD_INTERRUPT);
        } finally {
            saveToFile(order.getOutputFileName(), vocabulary.stream());
        }
    }

    private List<URL> initLatchAndGetURLs(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            List<URL> urls = lines
                    .filter(validateString())
                    .map(convertToURL())
                    .collect(Collectors.toList());
            producersDoneSignal = new CountDownLatch(urls.size());
            return urls;
        }
    }

    private void runWordsCollectors() {
        Environment environment = new Environment(queue, vocabulary, producersDoneSignal);
        IntStream
                .range(0, VocabularyMaker.COLLECTORS_AMOUNT)
                .forEach(n -> CompletableFuture
                        .runAsync(() -> new WordsCollector(environment).collectWords())
                        .exceptionally(throwable -> {
                            logger.error(ERROR_DURING_COLLECTING);
                            return null;
                        })
                        .thenAccept(t -> consumersDoneSignal.countDown()));
    }

    public void runUrlHandlers(List<URL> urlList) {
        urlList.forEach(url -> CompletableFuture
                .runAsync(() -> new UrlHandler(queue).collectWords(url))
                .applyToEither(failAfter(), Function.identity())
                .thenAccept(result -> logger.info("{} {}", THREAD_SUCCESS, url))
                .exceptionally(throwable -> {
                    logger.error("{} {}", throwable.getCause().getMessage(), url);
                    return null;
                })
                .thenAccept(t -> producersDoneSignal.countDown()));
    }

    private <T> CompletableFuture<T> failAfter() {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException(TIMED_OUT);
            return promise.completeExceptionally(ex);
        }, TIMEOUT_VALUE, TIMEOUT_UNIT);
        return promise;
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
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
            return validator.isValid(s);
        };
    }
}
