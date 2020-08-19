package com.siberteam.vtungusov.fileworker;

import com.siberteam.vtungusov.model.Order;
import com.siberteam.vtungusov.model.SorterData;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
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
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    private static final String POSTFIX_DELIMITER = "_";
    public static final String INTERRUPT_WITH = "Thread was interrupted due sorting with ";
    public static final String WRITE_ERROR = "Error during file write ";
    public static final String TIMED_OUT = "Task timed out";
    public static final String EXTENSION_DELIMITER = ".";
    public static final int TIMEOUT_VALUE = 1;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.MINUTES;
    private final SorterFactory sorterFactory;

    public FileWorker(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sortFile(Order order) throws IOException {
        checkInputFile(order.getInputFileName());
        ExecutorService executorService = Executors.newFixedThreadPool(order.getThreadCount());
        Map<SorterData, Future<?>> futureMap = order.getSortersDataSet().parallelStream()
                .collect(getFutureMap(order, executorService));
        futureMap.entrySet().parallelStream()
                .forEach(setTimeOut());
        executorService.shutdown();
    }

    private void sort(Order order, SorterData sorterData, String outFileName) throws IOException, InstantiationException {
        Stream<String> prepareData = getPreparedData(order.getInputFileName());
        Stream<String> sortedStream = getSortedWords(prepareData, order, sorterData);
        saveToFile(outFileName, sortedStream);
    }

    private void saveToFile(String outFileName, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(outFileName), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new RuntimeException(WRITE_ERROR + outFileName);
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

    private Stream<String> getSortedWords(Stream<String> wordStream, Order order, SorterData sorterData)
            throws InstantiationException {
        Sorter sorter = getSorter(sorterData.getConstructor());
        return sorter.sort(wordStream, order.getDirection());
    }

    private Predicate<String> notEmpty() {
        return s -> !s.isEmpty();
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }

    private Sorter getSorter(Constructor<? extends Sorter> constructor) throws
            InstantiationException {
        return sorterFactory.getSorter(constructor);
    }

    private Collector<SorterData, ?, Map<SorterData, Future<?>>> getFutureMap(Order order, ExecutorService executor) {
        return Collectors.toMap(sorterData -> sorterData,
                getFuture(order, executor),
                (f1, f2) -> f2);
    }

    private Function<SorterData, Future<?>> getFuture(Order order, ExecutorService executor) {
        return sorterData -> {
            try {
                String postfix = POSTFIX_DELIMITER + sorterData.getName();
                String outWithPostfix = addPostfix(order.getOutputFileName(), postfix);
                checkOutputFile(outWithPostfix);
                return executor.submit(getRunnable(order, sorterData, outWithPostfix));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        };
    }

    private Runnable getRunnable(Order order, SorterData sorterData, String outFileName) {
        return () -> {
            try {
                sort(order, sorterData, outFileName);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } catch (InstantiationException e) {
                throw new RuntimeException(DEFAULT_CONSTRUCTOR_EXPECTED + sorterData.getName());
            }
        };
    }

    private Consumer<? super Map.Entry<SorterData, Future<?>>> setTimeOut() {
        return o -> {
            try {
                o.getValue().get(TIMEOUT_VALUE, TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                throw new RuntimeException(INTERRUPT_WITH + o.getKey().getName());
            } catch (ExecutionException e) {
                throw new RuntimeException(DEFAULT_CONSTRUCTOR_EXPECTED + o.getKey().getName());
            } catch (TimeoutException e) {
                throw new RuntimeException(TIMED_OUT + o.getKey().getName());
            }
        };
    }

    private String addPostfix(String base, String postfix) {
        String result = base;
        if (base.contains(EXTENSION_DELIMITER)) {
            result = FilenameUtils.getBaseName(base) + postfix + EXTENSION_DELIMITER + FilenameUtils.getExtension(base);
        }
        return result;
    }
}
