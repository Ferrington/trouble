package org.trouble;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleIO implements IOHelper {
    Scanner scan;

    ConsoleIO() {
        scan = new Scanner(System.in);
    }

//    public static void printBoard(Board boardState) {
//        String board = boardTemplate;
//        board = formatDiePositions(board, boardState.getDieDisplay());
//
//        clearConsole();
//        System.out.print(board);
//    }

    //    private static String formatDiePositions(String board, int[] dieDisplay) {
//        for (int i = 0; i < dieDisplay.length; i++) {
//            String displayString;
//            if (dieDisplay[i] == -1)
//                displayString = " ";
//            else
//                displayString = String.valueOf(dieDisplay[i]);
//
//            board = board.replace("d" + i, displayString);
//        }
//
//        return board;
//    }
    public String prompt(String message) {
        System.out.print(message + " ");
        return scan.next();
    }

    public void printString(String str) {
        System.out.println(str);
    }

    public void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
            // I should probably do something here, but I don't know what yet
        }
    }
}
