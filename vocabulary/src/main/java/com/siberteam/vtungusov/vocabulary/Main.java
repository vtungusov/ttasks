package com.siberteam.vtungusov.vocabulary;

import com.siberteam.vtungusov.vocabulary.exception.BadArgumentsException;
import com.siberteam.vtungusov.vocabulary.handler.Anagramer;
import com.siberteam.vtungusov.vocabulary.handler.VocabularyMaker;
import com.siberteam.vtungusov.vocabulary.model.Order;
import com.siberteam.vtungusov.vocabulary.ui.UIManager;

import java.io.IOException;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished" +
            "\n-----------------------------";
    private static final String FILE_READING_ERROR = "Something wrong, file reading error";

    public static void main(String[] args) {
        UIManager uiManager = new UIManager();
        try {
            uiManager.handleOptions(args);
            VocabularyMaker vocabularyMaker = new VocabularyMaker();
            String outputFileName = uiManager.getOutputFileName();
            final long st1 = System.currentTimeMillis();
            vocabularyMaker.collectVocabulary(new Order(uiManager.getInputFileName(),
                    outputFileName, uiManager.getCollectorsCount()));
            System.out.println("Collected in: " + (System.currentTimeMillis() - st1) + " milliseconds");
            final long st = System.currentTimeMillis();
            new Anagramer().findAnagrams(outputFileName);
            System.out.println("Finished in: " + (System.currentTimeMillis() - st) + " milliseconds");
            System.out.println(SUCCESSFULLY_FINISHED);
        } catch (IOException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            } else System.out.println(FILE_READING_ERROR);
        } catch (BadArgumentsException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
