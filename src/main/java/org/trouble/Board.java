package org.trouble;

import java.util.Random;

public class Board {
    // color ascii codes
    final String RESET = "\033[0m";
    final String RED = "\033[0;31m";
    final String GREEN = "\033[0;32m";
    final String YELLOW = "\033[0;33m";
    final String BLUE = "\033[0;34m";

    final String boardTemplate =
            YELLOW + "  o   o   o   " + RESET + "o" + BLUE + "   o   o   o  \n" +
                    YELLOW + "o o                       " + BLUE + "o o\n" +
                    YELLOW + "    o                   " + BLUE + "o    \n" +
                    YELLOW + "o     o               " + BLUE + "o     o\n" +
                    YELLOW + "        o           " + BLUE + "o        \n" +
                    YELLOW + "o           " + RESET + "#####           " + BLUE + "o\n" +
                    RESET + "           # d0 d2 #           \n" +
                    "o          #  d4  #          o\n" +
                    "           # d3 d1 #           \n" +
                    GREEN + "o           " + RESET + "#####           " + RED + "o\n" +
                    GREEN + "        o           " + RED + "o        \n" +
                    GREEN + "o     o               " + RED + "o     o\n" +
                    GREEN + "    o                   " + RED + "o    \n" +
                    GREEN + "o o                       " + RED + "o o\n" +
                    GREEN + "  o   o   o   " + RESET + "o   " + RED + "o   o   o  \n" + RESET;

    IOHelper ioHelper;
    Random rand = new Random();

    // state variables
    int[] normalSpaces = new int[28];
    int[] finishLineSpaces = new int[16];
    int[] homePegs = {4, 4, 4, 4};
    int[] dieDisplay = new int[5];

    Board(IOHelper ioHelper) {
        this.ioHelper = ioHelper;
    }

    public int[] getDieDisplay() {
        return dieDisplay;
    }

    public void setDieDisplay(int[] diceDisplay) {
        this.dieDisplay = diceDisplay;
    }

    //    private int rollDieSequence() {
//        int[] dieDisplayTemplate = {-1,-1,-1,-1,-1};
//        int rollResult = 1;
//        int prevResult = -1;
//
//        for (int i = 0; i < 5; i++) {
//            int[] dieDisplay = dieDisplayTemplate.clone();
//
//            rollResult = rollDie(prevResult);
//            prevResult = rollResult;
//
//            dieDisplay[i] = rollResult;
//
//            board.setDieDisplay(dieDisplay);
//            IOHelper.printBoard(board);
//            sleep(400);
//        }
//
//        return rollResult;
//    }

    private int rollDie(int prevResult) {
        int rollResult;
        do {
            rollResult = rand.nextInt(6) + 1;
        } while (rollResult == prevResult);

        return rollResult;
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
