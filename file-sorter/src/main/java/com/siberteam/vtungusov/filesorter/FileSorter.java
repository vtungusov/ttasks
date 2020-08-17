package com.siberteam.vtungusov.filesorter;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.sorter.SorterFactory.DEFAULT_CONSTRUCTOR_EXPECTED;
import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileSorter {
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    private static final String POSTFIX_DELIMITER = "_";
    public static final String INTERRUPT_EXCEPTION = "Interrupt exception due sorting with ";
    public static final String WRITE_EXCEPTION = "Exception during file write ";
    public static final String TIMED_OUT = "Task timed out";
    private final SorterFactory sorterFactory;

    public FileSorter(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sortFile(String inputFileName, String outputFileName, Set<Constructor<? extends Sorter>> constructors, Integer threadCount, SortDirection direction) throws IOException {
        checkInputFile(inputFileName);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Map<? extends Future<?>, ? extends Constructor<? extends Sorter>>
                futureMap = constructors.parallelStream()
                .collect(getFutureMap(inputFileName, outputFileName, direction, executorService));
        futureMap.entrySet().parallelStream()
                .forEach(getResult());
        executorService.shutdown();
    }

    private void sort(String inputFileName, String outputFileName, Constructor<? extends Sorter> constructor, SortDirection direction)
            throws IOException, InstantiationException {
        checkOutputFile(outputFileName);
        Stream<String> prepareData = prepareData(inputFileName);
        Stream<String> sortedStream = getSortedStream(prepareData, constructor, direction);
        saveToFile(outputFileName, sortedStream);
    }

    private void saveToFile(String outputFileName, Stream<String> stringStream) {
        stringStream
                .forEachOrdered(s -> {
                    try {
                        Files.write(Paths.get(outputFileName), (s + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        throw new RuntimeException(WRITE_EXCEPTION + outputFileName);
                    }
                });
    }

    private Stream<String> prepareData(String inputFileName) throws IOException {
        return Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream)
                .filter(notEmpty())
                .filter(notNumber())
                .map(String::toLowerCase);
    }

    private Stream<String> getSortedStream(Stream<String> wordStream, Constructor<? extends
            Sorter> constructor, SortDirection direction) throws InstantiationException {
        Sorter sorter = getSorter(constructor);
        return sorter.sort(wordStream, direction);
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

    private Collector<? super Constructor<? extends Sorter>, ?, ? extends Map<? extends Future<?>, ? extends Constructor<? extends Sorter>>> getFutureMap
            (String inputFileName, String outputFileName, SortDirection direction, ExecutorService executorService) {
        return Collectors.toMap(constructor -> {
            try {
                String postfix = POSTFIX_DELIMITER + constructor.getDeclaringClass().getSimpleName();
                String outWithPostfix = addPostfix(outputFileName, postfix);
                checkOutputFile(outWithPostfix);
                return executorService.submit(new SorterThread(constructor, inputFileName, outWithPostfix, direction));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, constructor -> constructor, (c1, c2) -> (c1));
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

    private class SorterThread implements Runnable {
        private final Constructor<? extends Sorter> constructor;
        private final String inputFileName;
        private final String outputFileName;
        private final SortDirection direction;

        public SorterThread(Constructor<? extends Sorter> constructor, String inputFileName, String outputFileName, SortDirection direction) {
            this.constructor = constructor;
            this.inputFileName = inputFileName;
            this.outputFileName = outputFileName;
            this.direction = direction;
        }

        @Override
        public void run() {
            try {
                sort(inputFileName, outputFileName, constructor, direction);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } catch (InstantiationException e) {
                throw new RuntimeException(DEFAULT_CONSTRUCTOR_EXPECTED + constructor.getClass().getSimpleName());
            }
        }
    }

    private String addPostfix(String base, String postfix) {
        String result;
        if (base.contains(".")) {
            String[] strings = base.split("\\.");
            StringJoiner joiner = new StringJoiner(".", "", postfix);
            for (int i = 0; i < strings.length - 1; i++) {
                joiner.add(strings[i]);
            }
            result = joiner.toString() + "." + strings[strings.length - 1];
        } else {
            result = base + postfix;
        }
        return result;
    }
}
