package com.siberteam.vtungusov.fileworker;

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
    public static final String INTERRUPT_EXCEPTION = "Interrupt exception due sorting with ";
    public static final String WRITE_EXCEPTION = "Exception during file write ";
    public static final String TIMED_OUT = "Task timed out";
    public static final String EXTENSION_DELIMITER = ".";
    private final SorterFactory sorterFactory;

    public FileWorker(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sortFile(SortOrder order) throws IOException {
        checkInputFile(order.getInputFileName());
        ExecutorService executorService = Executors.newFixedThreadPool(order.getThreadCount());
        Map<Future<?>, Constructor<? extends Sorter>> futureMap = order.getConstructors().parallelStream()
                .collect(getFutureMap(order, executorService));
        futureMap.entrySet().parallelStream()
                .forEach(getResult());
        executorService.shutdown();
    }

    private void sort(ThreadSortOrder order)
            throws IOException, InstantiationException {
        checkOutputFile(order.getOutputFileName());
        Stream<String> prepareData = prepareData(order.getInputFileName());
        Stream<String> sortedStream = getSortedStream(prepareData, order);
        saveToFile(order, sortedStream);
    }

    private void saveToFile(ThreadSortOrder order, Stream<String> stringStream) {
        try {
            Files.write(Paths.get(order.getOutputFileName()), (Iterable<String>) stringStream::iterator);
        } catch (IOException e) {
            throw new RuntimeException(WRITE_EXCEPTION + order.getOutputFileName());
        }
    }

    private Stream<String> prepareData(String inputFileName) throws IOException {
        return Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream)
                .filter(notEmpty())
                .filter(notNumber())
                .map(String::toLowerCase);
    }

    private Stream<String> getSortedStream(Stream<String> wordStream, ThreadSortOrder order)
            throws InstantiationException {
        Sorter sorter = getSorter(order.getConstructor());
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

    private Collector<? super Constructor<? extends Sorter>, ?, ? extends Map<Future<?>,
            Constructor<? extends Sorter>>> getFutureMap(SortOrder order, ExecutorService executorService) {
        return Collectors.toMap(getFuture(order, executorService),
                constructor -> constructor,
                (c1, c2) -> (c1));
    }

    private Function<Constructor<? extends Sorter>, ? extends Future<?>> getFuture(SortOrder order,
                                                                                   ExecutorService executorService) {
        return constructor -> {
            try {
                String postfix = POSTFIX_DELIMITER + constructor.getDeclaringClass().getSimpleName();
                String outWithPostfix = addPostfix(order.getOutputFileName(), postfix);
                checkOutputFile(outWithPostfix);
                return executorService.submit(getRunnable(order, constructor));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        };
    }

    private Runnable getRunnable(SortOrder order, Constructor<? extends Sorter> constructor) {
        return () -> {
            try {
                ThreadSortOrder threadOrder = new ThreadSortOrder(
                        order.getInputFileName(),
                        order.getOutputFileName(),
                        constructor,
                        order.getDirection());
                sort(threadOrder);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } catch (InstantiationException e) {
                throw new RuntimeException(DEFAULT_CONSTRUCTOR_EXPECTED + constructor.getClass().getSimpleName());
            }
        };
    }

    private Consumer<? super Map.Entry<? extends Future<?>, ? extends Constructor<? extends Sorter>>> getResult() {
        return o -> {
            try {
                o.getKey().get(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(INTERRUPT_EXCEPTION + o.getValue());
            } catch (ExecutionException e) {
                throw new RuntimeException(DEFAULT_CONSTRUCTOR_EXPECTED + o.getValue());
            } catch (TimeoutException e) {
                throw new RuntimeException(TIMED_OUT + o.getValue());
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
