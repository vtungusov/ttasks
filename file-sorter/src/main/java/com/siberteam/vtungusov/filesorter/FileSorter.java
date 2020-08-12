package com.siberteam.vtungusov.filesorter;

import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.siberteam.vtungusov.util.FileUtil.checkInputFile;
import static com.siberteam.vtungusov.util.FileUtil.checkOutputFile;

public class FileSorter {
    private static final String STRING_SPLIT_REGEX = "[^a-zA-Z]";
    //    private static final String STRING_SPLIT_REGEX1 = "()\\W+|\\d(?!\\w)|(?<!\\w)\\d";
    private Sorter sorter;

    public void sort(String inputFileName, String outputFileName, Class<? extends Sorter> sorterClass, SortDirection direction) throws IOException {
        validateFiles(inputFileName, outputFileName);
        initSorter(sorterClass);
        Stream<String> sortedWords = prepareAndSort(inputFileName, direction);
        saveToFile(outputFileName, sortedWords);
    }

    private void saveToFile(String outputFileName, Stream<String> sortedWords) throws IOException {
        List<String> stringList = sortedWords
                .collect(Collectors.toList());
        Files.write(Paths.get(outputFileName), stringList);
    }

    private Stream<String> prepareAndSort(String inputFileName, SortDirection direction) throws IOException {
        Stream<String> wordStream = Files.lines(Paths.get(inputFileName))
                .map(line -> line.trim().split(STRING_SPLIT_REGEX))
                .flatMap(Arrays::stream);
        return sorter.sort(wordStream, direction);
    }

    private void initSorter(Class<?> sorterClass) {
        try {
            Constructor<?> constructor = sorterClass.getConstructor();
            Object instance = constructor.newInstance();
            sorter = (Sorter) instance;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ignore) {
        }
    }

    private void validateFiles(String inputFileName, String outputFileName) throws IOException {
        checkInputFile(inputFileName);
        checkOutputFile(outputFileName);
    }
}
