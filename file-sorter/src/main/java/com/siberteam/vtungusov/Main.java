package com.siberteam.vtungusov;

import com.siberteam.vtungusov.filesorter.FileSorter;
import com.siberteam.vtungusov.sorter.SortDirection;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.sorter.SorterFactory;
import com.siberteam.vtungusov.ui.BadArgumentsException;
import com.siberteam.vtungusov.ui.UIManager;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report" +
            "\n-----------------------------";
    private static final String FILE_READING_ERROR = "Something wrong, file reading error";
    private static final String CONCURRENCY_ERROR = "Concurrency execution error";

    public static void main(String[] args) {
        SorterFactory sorterFactory = new SorterFactory();
        UIManager uiManager = new UIManager(sorterFactory);
        try {
            uiManager.handleOptions(args);
            String inputFileName = uiManager.getInputFileName();
            String outputFileName = uiManager.getOutputFileName();
            SortDirection direction = uiManager.getSortType();
            FileSorter fileSorter = new FileSorter(sorterFactory);
            if (uiManager.isMultiSorting()) {
                Integer threadCount = uiManager.getThreadCount();
                fileSorter
                        .multiSort(inputFileName, outputFileName, threadCount, direction);
            } else {
                Constructor<? extends Sorter> sorterClass = uiManager.getSorterConstructor();
                fileSorter
                        .sort(inputFileName, outputFileName, sorterClass, direction);
            }
            System.out.println(SUCCESSFULLY_FINISHED);
        } catch (BadArgumentsException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                System.out.println(FILE_READING_ERROR);
            }
        } catch (InstantiationException e) {
            System.out.println(e.getMessage());
        }
    }
}
