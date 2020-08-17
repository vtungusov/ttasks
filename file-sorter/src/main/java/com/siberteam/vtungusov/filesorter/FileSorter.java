package com.siberteam.vtungusov.filesorter;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import com.siberteam.vtungusov.ui.BadArgumentsException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileSorter {
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    private static final String POSTFIX_DELIMITER = "_";
    private final SorterFactory sorterFactory;

    public FileSorter(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sortFile(String inputFileName, String outputFileName, Constructor<? extends Sorter> constructor, SortDirection direction, Integer threadCount)
            throws IOException, InstantiationException {
        validateFiles(inputFileName, outputFileName);
        if (constructor != null) {
            sort(inputFileName, outputFileName, constructor, direction);
        } else {
            multiSort(inputFileName, outputFileName, threadCount, direction);
        }
    }

    private void sort(String inputFileName, String outputFileName, Constructor<? extends Sorter> constructor, SortDirection direction)
            throws IOException, InstantiationException {
        Stream<String> prepareData = prepareData(inputFileName);
        Stream<String> sortedStream = getSortedStream(prepareData, constructor, direction);
        saveToFile(outputFileName, sortedStream);
    }

    private void saveToFile(String outputFileName, Stream<String> stringStream) throws IOException {
        List<String> stringList = stringStream
                .collect(Collectors.toList());
        Files.write(Paths.get(outputFileName), stringList);
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

    private void validateFiles(String inputFileName, String outputFileName) throws IOException {
        checkInputFile(inputFileName);
        checkOutputFile(outputFileName);
    }

    private void multiSort(String inputFileName, String outputFileName, Integer threadCount, SortDirection direction) {
        ExecutorService executorService = new ForkJoinPool(threadCount);
        sorterFactory.getAllSorter().parallelStream()
                .map(getOptionalConstructor())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(executeInParallel(inputFileName,
                        outputFileName,
                        direction,
                        executorService));
    }

    private Consumer<? super Constructor<? extends Sorter>> executeInParallel(String inputFileName, String outputFileName, SortDirection direction, ExecutorService executorService) {
        return constructor -> {
            String postfix = POSTFIX_DELIMITER + constructor.getDeclaringClass().getSimpleName();
            String outWithPostfix = addPostfix(outputFileName, postfix);
            try {
                checkOutputFile(outWithPostfix);
                executorService.submit(new SorterThread(constructor, inputFileName, outWithPostfix, direction)).get();
            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
        };
    }

    private Function<Class<? extends Sorter>, Optional<? extends Constructor<? extends Sorter>>> getOptionalConstructor() {
        return sorter -> {
            Constructor<? extends Sorter> constructor = null;
            try {
                constructor = sorterFactory.getConstructor(sorter.getName());
            } catch (BadArgumentsException e) {
                System.out.println(e.getMessage());
            }
            return constructor != null ? Optional.of(constructor) : Optional.empty();
        };
    }

    private class SorterThread implements Callable<Boolean> {
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
        public Boolean call() throws IOException, InstantiationException {
            sort(inputFileName, outputFileName, constructor, direction);
            return true;
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
