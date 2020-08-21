package com.siberteam.vtungusov.vocabulary;

import com.siberteam.vtungusov.vocabulary.exception.BadArgumentsException;
import com.siberteam.vtungusov.vocabulary.fileworker.FileWorker;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.ui.UIManager;

import java.io.IOException;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report" +
            "\n-----------------------------";
    private static final String FILE_READING_ERROR = "Something wrong, file reading error";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        try {
            uiManager.handleOptions(args);
            FileWorker fileWorker = new FileWorker();

            fileWorker.createVocabulary(new Order(uiManager.getInputFileName(),
                    uiManager.getOutputFileName()));
            System.out.println(SUCCESSFULLY_FINISHED);
        } catch (IOException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else System.out.println(FILE_READING_ERROR);
        } catch (BadArgumentsException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
        }
    }
}