package com.siberteam.vtungusov;

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
        Class<?> sorterClass;
        try {
            uiManager.handleOptions(args);
            inputFileName = uiManager.getInputFileName();
            outputFileName = uiManager.getOutputFileName();
            sorterClass = uiManager.getSorterClass();
        } catch (BadArgumentsException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            return;
        }

        try {
            new FileSorter()
                    .sort(inputFileName, outputFileName, sorterClass);

            System.out.println(SUCCESSFULLY_FINISHED);
        } catch (IOException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else {
                System.out.println(FILE_READING_ERROR);
            }
        }
    }
}
