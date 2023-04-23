package org.trouble;

import java.util.Arrays;
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

        // @formatter:off
        this.boardTemplate =
            "[H00] [H01]                               [H10] [H11]\n" +
            "[H02] [H03]   [S0]   [S1]   [S2]   [S3]   [S4]   [S5]   [S6]   [H12] [H13] \n" +
            "      [0]   [1]   [2]   [3]   [4]   [5]   [6]   \n" +
            String.format("  [S27] [27] %s[F00]%s                       %s[F10]%s [7] [S7]\n", Y, END_Y, B, END_B) +
            String.format("        %s[F01]%s                   %s[F11]%s    \n", Y, END_Y, B, END_B) +
            String.format("  [S26] [26]     %s[F02]%s               %s[F12]%s     [8] [S8]\n", Y, END_Y, B, END_B) +
            String.format("            %s[F03]%s           %s[F13]%s        \n", Y, END_Y, B, END_B) +
            "  [S25] [25]           #####           [9] [S9]\n" +
            "               # d0 d2 #           \n" +
            "  [S24] [24]          #  d4  #          [10] [S10]\n" +
            "               # d3 d1 #           \n" +
            "  [S23] [23]           #####           [11] [S11]\n" +
            String.format("            %s[F33]%s           %s[F23]%s        \n", G, END_G, R, END_R) +
            String.format("  [S22] [22]     %s[F32]%s               %s[F22]%s     [12] [S12]\n", G, END_G, R, END_R) +
            String.format("        %s[F31]%s                   %s[F21]%s    \n", G, END_G, R, END_R) +
            String.format("  [S21] [21] %s[F30]%s                       %s[F20]%s [13] [S13]\n", G, END_G, R, END_R) +
            "      [20]   [19]   [18]   [17]   [16]   [15]   [14]   \n" +
            "[H30] [H31]   [S20]   [S19]   [S18]   [S17]   [S16]   [S15]   [S14]   [H20] [H21]\n" +
            "[H32] [H33]                               [H22] [H23]\n";
        // @formatter:on

        // FOR TESTING

        normalSpaces[7] = PlayerColor.YELLOW;
        normalSpaces[17] = PlayerColor.RED;
        normalSpaces[22] = PlayerColor.GREEN;

        normalSpaces[4] = PlayerColor.BLUE;
        normalSpaces[5] = PlayerColor.BLUE;
        normalSpaces[6] = PlayerColor.BLUE;
        homePegs[0] = 3;
        homePegs[1] = 1;
        homePegs[2] = 3;
        homePegs[3] = 3;
    }

    public void printBoard() {
        printBoard(null, 0);
    }

    public void printBoard(PlayerColor currentPlayer, int dieRoll) {
        String boardOutput = boardTemplate;
        boardOutput = formatDiePositions(boardOutput);
        boardOutput = formatNormalSpaces(boardOutput);
        boardOutput = formatFinishLines(boardOutput);
        boardOutput = formatHomePegs(boardOutput);
        boardOutput = formatPlayerSelectOptions(boardOutput, currentPlayer, dieRoll);


        ioHelper.clearConsole();
        ioHelper.printString(boardOutput);
    }

    public void printMoveOptions(PlayerColor currentPlayer, int dieRoll) {
        printBoard(currentPlayer, dieRoll);
    }

    public void generateMoveOptions(PlayerColor currentPlayer, int dieRoll) {
        PlayerColor[] relativeBoard = generateRelativeBoard(currentPlayer);

        int[] validMoves = new int[]{-1, -1, -1, -1};
        int validMovesIndex = 0;

        // can move a peg out if they rolled a 6 and they don't already have a peg there
        if (dieRoll == 6 && relativeBoard[0] != currentPlayer) {
            validMoves[validMovesIndex] = -2;
            validMovesIndex++;
        }

        for (int i = 0; i < relativeBoard.length; i++) {
            // only interested in current player's pegs
            if (relativeBoard[i] != currentPlayer) continue;

            // destination doesn't exist
            int desiredSpace = i + dieRoll;
            if (desiredSpace >= relativeBoard.length) continue;

            // currentPlayer already occupies destination
            PlayerColor playerOccupyingDestination = relativeBoard[desiredSpace];
            if (playerOccupyingDestination == currentPlayer) continue;

            // all checks satisfied, is valid move
            validMoves[validMovesIndex] = i;
            validMovesIndex++;
        }

        System.out.println(Arrays.toString(validMoves));
    }

    private PlayerColor[] generateRelativeBoard(PlayerColor currentPlayer) {
        int offset = currentPlayer.homePosition();
        PlayerColor[] relativeSpaces = new PlayerColor[normalSpaces.length + PEG_COUNT];
        for (int i = 0; i < normalSpaces.length; i++) {
            int translatedIndex = (i + normalSpaces.length - offset) % normalSpaces.length;
            relativeSpaces[translatedIndex] = normalSpaces[i];
        }

        for (int i = 0; i < PEG_COUNT; i++) {
            int translatedIndex = normalSpaces.length + i;
            relativeSpaces[translatedIndex] = finishLineSpaces[currentPlayer.intValue][i];
        }

        return relativeSpaces;
    }

    private String formatPlayerSelectOptions(String boardOutput, PlayerColor currentPlayer, int dieRoll) {
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

    public int rollDieSequence() {
        int[] dieDisplayTemplate = {-1, -1, -1, -1, -1};
        int rollResult = 1;
        int prevResult = -1;

        for (int i = 0; i < 5; i++) {
            dieDisplay = dieDisplayTemplate.clone();

            rollResult = rollDie(prevResult);
            prevResult = rollResult;

            dieDisplay[i] = rollResult;

            printBoard();
            sleep(400);
        }

        return rollResult;
    }

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
