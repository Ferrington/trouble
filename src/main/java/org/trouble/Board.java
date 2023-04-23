package org.trouble;

import java.util.Random;

public class Board {
    // color tags
    final String Y;
    final String END_Y;
    final String B;
    final String END_B;
    final String R;
    final String END_R;
    final String G;
    final String END_G;
    final String boardTemplate;

    final int PEG_COUNT = 4;
    final int NORMAL_SPACE_COUNT = 28;

    IOHelper ioHelper;
    Random rand;

    // state variables
    PlayerColor[] normalSpaces = new PlayerColor[NORMAL_SPACE_COUNT];
    PlayerColor[][] finishLineSpaces = new PlayerColor[PlayerColor.values().length][PEG_COUNT];
    int[] homePegs = {PEG_COUNT, PEG_COUNT, PEG_COUNT, PEG_COUNT};
    int[] dieDisplay = {-1, -1, -1, -1, -1};

    Board(IOHelper ioHelper) {
        this.ioHelper = ioHelper;
        this.rand = new Random();

        this.Y = PlayerColor.YELLOW.openTag();
        this.END_Y = PlayerColor.YELLOW.closeTag();
        this.B = PlayerColor.BLUE.openTag();
        this.END_B = PlayerColor.BLUE.closeTag();
        this.R = PlayerColor.RED.openTag();
        this.END_R = PlayerColor.RED.closeTag();
        this.G = PlayerColor.GREEN.openTag();
        this.END_G = PlayerColor.GREEN.closeTag();

        this.boardTemplate =
                "[H00] [H01]                               [H10] [H11]\n" +
                        "[H02] [H03]   [S0]   [S1]   [S2]   [S3]   [S4]   [S5]   [S6]   [H12] [H13] \n" +
                        "      [0]   [1]   [2]   [3]   [4]   [5]   [6]   \n" +
                        "  [S27] [27] " + Y + "[F00]" + END_Y + "                       " + B + "[F10]" + END_B + " [7] [S7]\n" +
                        "        " + Y + "[F01]" + END_Y + "                   " + B + "[F11]" + END_B + "    \n" +
                        "  [S26] [26]     " + Y + "[F02]" + END_Y + "               " + B + "[F12]" + END_B + "     [8] [S8]\n" +
                        "            " + Y + "[F03]" + END_Y + "           " + B + "[F13]" + END_B + "        \n" +
                        "  [S25] [25]           #####           [9] [S9]\n" +
                        "               # d0 d2 #           \n" +
                        "  [S24] [24]          #  d4  #          [10] [S10]\n" +
                        "               # d3 d1 #           \n" +
                        "  [S23] [23]           #####           [11] [S11]\n" +
                        "            " + G + "[F33]" + END_G + "           " + R + "[F23]" + END_R + "        \n" +
                        "  [S22] [22]     " + G + "[F32]" + END_G + "               " + R + "[F22]" + END_R + "     [12] [S12]\n" +
                        "        " + G + "[F31]" + END_G + "                   " + R + "[F21]" + END_R + "    \n" +
                        "  [S21] [21] " + G + "[F30]" + END_G + "                       " + R + "[F20]" + END_R + " [13] [S13]\n" +
                        "      [20]   [19]   [18]   [17]   [16]   [15]   [14]   \n" +
                        "[H30] [H31]   [S20]   [S19]   [S18]   [S17]   [S16]   [S15]   [S14]   [H20] [H21]\n" +
                        "[H32] [H33]                               [H22] [H23]\n";

        // FOR TESTING
        normalSpaces[1] = PlayerColor.BLUE;
        normalSpaces[7] = PlayerColor.YELLOW;
        normalSpaces[8] = PlayerColor.BLUE;
        normalSpaces[15] = PlayerColor.BLUE;
        normalSpaces[17] = PlayerColor.RED;
        normalSpaces[22] = PlayerColor.GREEN;
        normalSpaces[25] = PlayerColor.BLUE;
        homePegs[0] = 3;
        homePegs[1] = 0;
        homePegs[2] = 3;
        homePegs[3] = 3;
    }

    public void printBoard() {
        String boardOutput = boardTemplate;
        boardOutput = formatDiePositions(boardOutput);
        boardOutput = formatNormalSpaces(boardOutput);
        boardOutput = formatPlayerSelectOptions(boardOutput, PlayerColor.BLUE);
        boardOutput = formatFinishLines(boardOutput);
        boardOutput = formatHomePegs(boardOutput);

        ioHelper.clearConsole();
        ioHelper.printString(boardOutput);
    }

    private String formatPlayerSelectOptions(String boardOutput, PlayerColor currentPlayer) {
        int selectionNumber = 1;
        for (int i = 0; i < normalSpaces.length; i++) {
            String replaceString = String.format("[S%s]", i);
            String displayString;
            if (normalSpaces[i] == currentPlayer && currentPlayer != null) {
                displayString = String.format(
                        "%s%s%s",
                        normalSpaces[i].openTag(),
                        selectionNumber,
                        normalSpaces[i].closeTag()
                );
                selectionNumber++;
            } else {
                displayString = " ";
            }


            boardOutput = boardOutput.replace(replaceString, displayString);
        }

        return boardOutput;
    }

    private String formatHomePegs(String boardOutput) {
        for (int i = 0; i < homePegs.length; i++) {
            for (int j = 0; j < PEG_COUNT; j++) {
                String replaceString = String.format("[H%s%s]", i, j);
                String displayString;
                if (j < homePegs[i]) {
                    displayString = String.format(
                            "%s%s%s",
                            PlayerColor.values()[i].openTag(),
                            (char) 0x25A0,
                            PlayerColor.values()[i].closeTag()
                    );
                } else {
                    displayString = " ";
                }

                boardOutput = boardOutput.replace(replaceString, displayString);
            }
        }

        return boardOutput;
    }

    private String formatFinishLines(String boardOutput) {
        for (int i = 0; i < finishLineSpaces.length; i++) {
            for (int j = 0; j < finishLineSpaces[i].length; j++) {
                String replaceString = String.format("[F%s%s]", i, j);
                String displayString;
                if (finishLineSpaces[i][j] == null)
                    displayString = "" + (char) 0x00B7;
                else
                    displayString = String.format(
                            "%s%s%s",
                            finishLineSpaces[i][j].openTag(),
                            (char) 0x25A0,
                            finishLineSpaces[i][j].closeTag()
                    );

                boardOutput = boardOutput.replace(replaceString, displayString);
            }
        }

        return boardOutput;
    }

    private String formatNormalSpaces(String boardOutput) {
        for (int i = 0; i < normalSpaces.length; i++) {
            String replaceString = String.format("[%s]", i);
            String displayString;
            if (normalSpaces[i] == null)
                displayString = "" + (char) 0x00B7;
            else
                displayString = String.format(
                        "%s%s%s",
                        normalSpaces[i].openTag(),
                        (char) 0x25A0,
                        normalSpaces[i].closeTag()
                );

            boardOutput = boardOutput.replace(replaceString, displayString);
        }

        return boardOutput;
    }

    private String formatDiePositions(String boardOutput) {
        for (int i = 0; i < dieDisplay.length; i++) {
            String displayString;
            if (dieDisplay[i] == -1)
                displayString = " ";
            else
                displayString = String.valueOf(dieDisplay[i]);

            boardOutput = boardOutput.replace("d" + i, displayString);
        }

        return boardOutput;
    }

//    private int rollDieSequence() {
//        int[] dieDisplayTemplate = {-1, -1, -1, -1, -1};
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
//
//    public int rollDie() {
//        int rollResult;
//        do {
//            rollResult = rand.nextInt(6) + 1;
//        } while (rollResult == prevResult);
//
//        return rollResult;
//    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}