package com.siberteam.vtungusov.filesorter;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import com.siberteam.vtungusov.ui.BadArgumentsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileSorter {
    private static final String STRING_SPLIT_REGEX = "\\W";

    public void sort(String inputFileName, String outputFileName, Class<?> clazz, SortDirection direction) throws IOException, BadArgumentsException {
        validateFiles(inputFileName, outputFileName);
        initSorter(clazz);
        Stream<String> sortedWords = prepareAndSort(inputFileName, clazz, direction);
        saveToFile(outputFileName, sortedWords);
    }

    private void saveToFile(String outputFileName, Stream<String> sortedWords) throws IOException {
        List<String> stringList = sortedWords
                .collect(Collectors.toList());
        Files.write(Paths.get(outputFileName), stringList);
    }

    private Stream<String> prepareAndSort(String inputFileName, Class<?> clazz, SortDirection direction) throws IOException, BadArgumentsException {
        Stream<String> wordStream = Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream)
                .filter(s -> {
                    boolean isEmpty = s.toCharArray().length < 1;
                    boolean isDigit = s.chars()
                            .allMatch(Character::isDigit);
                    boolean isSpace = s.chars()
                            .anyMatch(Character::isSpaceChar);
                    return !isEmpty && !isDigit && !isSpace;
                });
        Sorter sorter = initSorter(clazz);
        return sorter.sort(wordStream, direction);
    }

    private Sorter initSorter(Class<?> sorterClass) throws BadArgumentsException {
        SorterFactory factory = new SorterFactory();
        return factory.createSorter(sorterClass);
    }

    private void validateFiles(String inputFileName, String outputFileName) throws IOException {
        checkInputFile(inputFileName);
        checkOutputFile(outputFileName);
    }
}
