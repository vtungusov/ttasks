package com.siberteam.vtungusov;

import com.siberteam.vtungusov.exception.BadArgumentsException;
import com.siberteam.vtungusov.fileworker.FileWorker;
import com.siberteam.vtungusov.model.Order;
import com.siberteam.vtungusov.sorter.SorterFactory;
import com.siberteam.vtungusov.ui.UIManager;

import java.io.IOException;

public class Main {
    private static final String SUCCESSFULLY_FINISHED = "Program successfully finished\nYou can look report" +
            "\n-----------------------------";
    private static final String FILE_READING_ERROR = "Something wrong, file reading error";

    public static void main(String[] args) {
        SorterFactory sorterFactory = new SorterFactory();
        UIManager uiManager = new UIManager(sorterFactory);
        try {
            uiManager.handleOptions(args);
            Order order = new Order(
                    uiManager.getInputFileName(),
                    uiManager.getOutputFileName(),
                    uiManager.getSortersData(),
                    uiManager.getThreadCount(),
                    uiManager.getSortType()
            );
            new FileWorker(sorterFactory)
                    .sortFile(order);
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
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
