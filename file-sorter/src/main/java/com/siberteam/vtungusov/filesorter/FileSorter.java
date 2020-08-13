package com.siberteam.vtungusov.filesorter;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileSorter {
    private static final String STRING_SPLIT_REGEX = "[\\W_&&[^ЁёА-я]]";
    private final SorterFactory sorterFactory;

    public FileSorter(SorterFactory sorterFactory) {
        this.sorterFactory = sorterFactory;
    }

    public void sort(String inputFileName, String outputFileName, Constructor<? extends Sorter> constructor, SortDirection direction)
            throws IOException, InstantiationException {
        validateFiles(inputFileName, outputFileName);
        initSorter(constructor);
        Stream<String> sortedWords = prepareAndSort(inputFileName, constructor, direction);
        saveToFile(outputFileName, sortedWords);
    }

    private void saveToFile(String outputFileName, Stream<String> sortedWords) throws IOException {
        List<String> stringList = sortedWords
                .collect(Collectors.toList());
        Files.write(Paths.get(outputFileName), stringList);
    }

    private Stream<String> prepareAndSort(String inputFileName, Constructor<? extends Sorter> constructor, SortDirection direction)
            throws IOException, InstantiationException {
        Stream<String> wordStream = Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream)
                .filter(notEmpty())
                .filter(notNumber())
                .map(String::toLowerCase);
        Sorter sorter = initSorter(constructor);
        return sorter.sort(wordStream, direction);
    }

    private Predicate<String> notEmpty() {
        return s -> !s.isEmpty();
    }

    private Predicate<String> notNumber() {
        return s -> !(s.chars()
                .allMatch(Character::isDigit));
    }

    private Sorter initSorter(Constructor<? extends Sorter> constructor) throws InstantiationException {
        return sorterFactory.getSorter(constructor);
    }

    private void validateFiles(String inputFileName, String outputFileName) throws IOException {
        checkInputFile(inputFileName);
        checkOutputFile(outputFileName);
    }
}
