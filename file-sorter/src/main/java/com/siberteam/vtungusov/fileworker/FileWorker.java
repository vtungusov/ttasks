package com.siberteam.vtungusov.fileworker;

import com.siberteam.vtungusov.exception.FileIOException;
import com.siberteam.vtungusov.exception.InitializationException;
import com.siberteam.vtungusov.exception.ThreadException;
import com.siberteam.vtungusov.model.Order;
import com.siberteam.vtungusov.model.SorterData;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.sorter.SorterFactory.DEFAULT_CONSTRUCTOR_EXPECTED;
import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileWorker {
    public static final String INTERRUPT_WITH = "Thread was interrupted due sorting with ";
    public static final String WRITE_ERROR = "Error during file writing ";
    public static final String TIMED_OUT = "Task timed out";
    public static final String EXTENSION_DELIMITER = ".";
    public static final int TIMEOUT_VALUE = 1;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    public static final String FILE_HEADER_PREFIX = "ORIGINAL (";
    public static final String FILE_HEADER_POSTFIX = ")";
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    private static final String POSTFIX_DELIMITER = "_";
    private final SorterFactory sorterFactory;

    public FileWorker(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sortFile(Order order) throws IOException {
        checkInputFile(order.getInputFileName());
        ExecutorService executorService = Executors.newFixedThreadPool(order.getThreadCount());
        Map<SorterData, Future<?>> futureMap = order.getSortersDataSet().parallelStream()
                .collect(getSubmittedTasks(order, executorService));
        futureMap.entrySet().parallelStream()
                .forEach(setTimeOut());
        executorService.shutdown();
    }

    private void sort(Order order, SorterData sorterData, String outFileName) throws IOException {
        Stream<String> prepareData = getPreparedData(order.getInputFileName());
        Stream<String> sortedStream = getSortedWords(prepareData, order, sorterData);
        saveToFile(outFileName, sortedStream);
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new FileIOException(WRITE_ERROR + outFileName);
        }
    }

    private Stream<String> getPreparedData(String inputFileName) throws IOException {
        return Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream)
                .filter(notEmpty())
                .filter(notNumber())
                .map(String::toLowerCase);
    }

    private Stream<String> getSortedWords(Stream<String> wordStream, Order order, SorterData sorterData) {
        Sorter sorter = sorterFactory.getSorter(sorterData.getConstructor());
        Stream<String> sortedStream = sorter.sort(wordStream, order.getDirection());

        return Stream.concat(Stream.of(FILE_HEADER_PREFIX + sorter.getName() + FILE_HEADER_POSTFIX), sortedStream);
    }

    private Predicate<String> notEmpty() {
        return s -> !s.isEmpty();
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }

    private Collector<SorterData, ?, Map<SorterData, Future<?>>> getSubmittedTasks(Order order, ExecutorService executor) {
        return Collectors.toMap(sorterData -> sorterData,
                createAndSubmitTask(order, executor),
                (f1, f2) -> f2);
    }

    private Function<SorterData, Future<?>> createAndSubmitTask(Order order, ExecutorService executor) {
        return sorterData -> {
            try {
                String outWithPostfix = addPostfix(order, sorterData);
                checkOutputFile(outWithPostfix);
                return executor.submit(() -> {
                    try {
                        sort(order, sorterData, outWithPostfix);
                    } catch (IOException e) {
                        throw new FileIOException(e.getMessage());
                    }
                });
            } catch (IOException e) {
                throw new FileIOException(e.getMessage());
            }
        };
    }

    private Consumer<Map.Entry<SorterData, Future<?>>> setTimeOut() {
        return o -> {
            try {
                o.getValue().get(TIMEOUT_VALUE, TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                throw new ThreadException(INTERRUPT_WITH + o.getKey().getName());
            } catch (ExecutionException e) {
                throw new InitializationException(DEFAULT_CONSTRUCTOR_EXPECTED + o.getKey().getName());
            } catch (TimeoutException e) {
                throw new ThreadException(TIMED_OUT + o.getKey().getName());
            }
        };
    }

    private String addPostfix(Order order, SorterData sorterData) {
        String base = order.getOutputFileName();
        String postfix = POSTFIX_DELIMITER + FilenameUtils.getExtension(sorterData.getName());
        String result = base + postfix;
        if (base.contains(EXTENSION_DELIMITER)) {
            result = FilenameUtils.getBaseName(base) + postfix + EXTENSION_DELIMITER + FilenameUtils.getExtension(base);
        }
        return result;
    }
}
