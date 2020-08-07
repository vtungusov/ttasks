package com.siberteam.vtungusov;

import com.siberteam.vtungusov.filesorter.FileSorter;
import com.siberteam.vtungusov.sorter.Sorter;
import com.siberteam.vtungusov.ui.BadArgumentsException;
import com.siberteam.vtungusov.ui.UIManager;

import java.io.IOException;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report" +
            "\n-----------------------------";
    private static final String FILE_READING_ERROR = "Something wrong, file reading error";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        String inputFileName;
        String outputFileName;
        Class<? extends Sorter> sorterClass;
        boolean descSort;
        try {
            uiManager.handleOptions(args);
            inputFileName = uiManager.getInputFileName();
            outputFileName = uiManager.getOutputFileName();
            sorterClass = uiManager.getSorterClass();
            descSort = uiManager.getSortType();
            new FileSorter()
                    .sort(inputFileName, outputFileName, sorterClass, descSort);
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
        }
    }
}
