package com.siberteam.vtungusov;

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
    private static final String STRING_SPLIT_REGEX = "[- ,\uFEFF\\]#$()/.\"'`“%”:;?!*0-9]";
    private Sorter sorter;

    private static String[] split(String s) {
        return s.split(STRING_SPLIT_REGEX);
    }

    public void sort(String inputFileName, String outputFileName, Class<?> sorterClass) throws IOException {
        validateFiles(inputFileName, outputFileName);
        initSorter(sorterClass);

        Stream<String> sortedWords = prepareAndSort(inputFileName);

        saveToFile(outputFileName, sortedWords);
    }

    private void saveToFile(String outputFileName, Stream<String> sortedWords) throws IOException {
        List<String> stringList = sortedWords.collect(Collectors.toList());
        Files.write(Paths.get(outputFileName), stringList);
    }

    private Stream<String> prepareAndSort(String inputFileName) throws IOException {
        Stream<String> wordStream = Files.lines(Paths.get(inputFileName))
                .map(FileSorter::split)
                .flatMap(Arrays::stream);
        return sorter.sort(wordStream);
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
