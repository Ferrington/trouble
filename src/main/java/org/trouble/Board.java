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

        // @formatter:off
        this.boardTemplate =
            "[h0] [H00] [H01]                               [H10] [H11] [h1]\n" +
            "  [H02] [H03]   [S0]   [S1]   [S2]   [S3]   [S4]   [S5]   [S6]   [H12] [H13] \n" +
            "        [0]   [1]   [2]   [3]   [4]   [5]   [6]   \n" +
            String.format("    [S27] [27] %s[F00]%s [SF00]                   [SF10] %s[F10]%s [7] [S7]\n", Y, END_Y, B, END_B) +
            String.format("          %s[F01]%s [SF01]               [SF11] %s[F11]%s    \n", Y, END_Y, B, END_B) +
            String.format("    [S26] [26]     %s[F02]%s [SF02]           [SF12] %s[F12]%s     [8] [S8]\n", Y, END_Y, B, END_B) +
            String.format("              %s[F03]%s [SF03]       [SF13] %s[F13]%s        \n", Y, END_Y, B, END_B) +
            "    [S25] [25]           #####           [9] [S9]\n" +
            "                 # d0 d2 #           \n" +
            "    [S24] [24]          #  d4  #          [10] [S10]\n" +
            "                 # d3 d1 #           \n" +
            "    [S23] [23]           #####           [11] [S11]\n" +
            String.format("              %s[F33]%s [SF33]       [SF23] %s[F23]%s        \n", G, END_G, R, END_R) +
            String.format("    [S22] [22]     %s[F32]%s [SF32]           [SF22] %s[F22]%s     [12] [S12]\n", G, END_G, R, END_R) +
            String.format("          %s[F31]%s [SF31]               [SF21] %s[F21]%s    \n", G, END_G, R, END_R) +
            String.format("    [S21] [21] %s[F30]%s [SF30]                   [SF20] %s[F20]%s [13] [S13]\n", G, END_G, R, END_R) +
            "        [20]   [19]   [18]   [17]   [16]   [15]   [14]   \n" +
            "[h3] [H30] [H31]   [S20]   [S19]   [S18]   [S17]   [S16]   [S15]   [S14]   [H20] [H21] [h2]\n" +
            "  [H32] [H33]                               [H22] [H23]\n";
        // @formatter:on
    }

    public void printBoard() {
        printBoard(new int[]{-1, -1, -1, -1}, null);
    }

    public void printBoard(int[] moveOptions, PlayerColor currentPlayer) {
        String boardOutput = boardTemplate;
        boardOutput = formatDiePositions(boardOutput);
        boardOutput = formatNormalSpaces(boardOutput);
        boardOutput = formatFinishLines(boardOutput);
        boardOutput = formatHomePegs(boardOutput);
        boardOutput = formatPlayerSelectOptions(boardOutput, moveOptions, currentPlayer);


        ioHelper.clearConsole();
        ioHelper.printString(boardOutput);
    }

    public PlayerColor whoWon() {
        for (int i = 0; i < finishLineSpaces.length; i++) {
            boolean allFinishLineSpacesFull = true;
            for (int j = 0; j < finishLineSpaces[i].length; j++) {
                if (finishLineSpaces[i][j] == null) {
                    allFinishLineSpacesFull = false;
                    break;
                }
            }
            if (allFinishLineSpacesFull)
                return PlayerColor.values()[i];
        }

        return null;
    }

    public void movePlayer(PlayerColor currentPlayer, int dieRoll) {

        int[] relativeMoveOptions = generateMoveOptions(currentPlayer, dieRoll);

        if (!validMoveExists(relativeMoveOptions)) {
            ioHelper.printString("No valid move exists.");
            ioHelper.prompt("Press [Enter] when you have recovered from your disappointment.");
            return;
        }

        int[] absoluteMoveOptions = relativePositionsToAbsolute(relativeMoveOptions, currentPlayer);
        printBoard(absoluteMoveOptions, currentPlayer);

        int playerMove = getPlayerMove(relativeMoveOptions, currentPlayer);
        movePeg(playerMove, relativeMoveOptions, currentPlayer, dieRoll);
        printBoard();
    }

    private void movePeg(int playerMove, int[] relativeMoveOptions, PlayerColor currentPlayer, int dieRoll) {
        PlayerColor[] relativeBoard = generateRelativeBoard(currentPlayer);

        if (playerMove == 0) { // move out from home
            if (relativeBoard[0] != null)
                homePegs[relativeBoard[0].intValue]++;

            homePegs[currentPlayer.intValue]--;
            relativeBoard[0] = currentPlayer;
        } else { // normal move
            int startPosition = -100;
            int moveCount = 1;
            for (int i = 0; i < relativeMoveOptions.length; i++) {
                if (relativeMoveOptions[i] < 0) continue;
                if (moveCount == playerMove)
                    startPosition = relativeMoveOptions[i];

                moveCount++;
            }

            int endPosition = startPosition + dieRoll;

            if (relativeBoard[endPosition] != null)
                homePegs[relativeBoard[endPosition].intValue]++;

            relativeBoard[startPosition] = null;
            relativeBoard[endPosition] = currentPlayer;
        }

        updateAbsoluteBoard(relativeBoard, currentPlayer);
    }

    private boolean validMoveExists(int[] options) {
        boolean validMoveExists = false;
        for (int i = 0; i < options.length; i++) {
            if (options[i] != -1) {
                validMoveExists = true;
                break;
            }
        }
        return validMoveExists;
    }

    private int countOptions(int[] moveOptions) {
        int optionCount = 0;
        for (int i = moveOptions.length - 1; i >= 0; i--) {
            if (moveOptions[i] >= 0)
                optionCount++;
        }

        return optionCount;
    }

    private int getPlayerMove(int[] moveOptions, PlayerColor currentPlayer) {
        boolean homeMoveAllowed = contains(moveOptions, -2);
        String homeMoveString = homeMoveAllowed ? "(0) to move a peg onto the board or " : "";

        String normalMoveString = "";
        int optionCount = countOptions(moveOptions);
        int moveCount = 0;
        for (int i = 0; i < moveOptions.length; i++) {
            if (moveOptions[i] < 0) continue;

            if (optionCount == 1) {
                normalMoveString += String.format("(%s) ", moveCount + 1);
            } else if (optionCount == 2) {
                if (moveCount == 0)
                    normalMoveString += String.format("(%s) ", moveCount + 1);
                else
                    normalMoveString += String.format("or (%s) ", moveCount + 1);
            } else if (optionCount >= 3) {
                if (moveCount < optionCount)
                    normalMoveString += String.format("(%s), ", moveCount + 1);
                else
                    normalMoveString += String.format("or (%s) ", moveCount + 1);
            }
            moveCount++;
        }
        // Select (0) to move a peg onto the board or (1), (2), or (3) to move a piece.

        String message = String.format(
                "%sSelect %s%sto move a piece.\nPress [Enter] to confirm.%s",
                currentPlayer.openTag(),
                homeMoveString,
                normalMoveString,
                currentPlayer.closeTag()
        );

        int move = -1;
        boolean validMove = false;
        while (!validMove) {
            try {
                String response = ioHelper.prompt(message);
                move = Integer.parseInt(response);

                boolean isValidHomeMove = move == 0 && homeMoveAllowed;
                boolean isValidNormalMove = move > 0 && move <= moveCount;
                validMove = isValidHomeMove || isValidNormalMove;
            } catch (NumberFormatException e) {

            }
            if (!validMove)
                ioHelper.printString("Please enter a valid move.");
        }

        return move;
    }

    private int[] generateMoveOptions(PlayerColor currentPlayer, int dieRoll) {
        PlayerColor[] relativeBoard = generateRelativeBoard(currentPlayer);

        int[] validMoves = new int[]{-1, -1, -1, -1};
        int validMovesIndex = 0;

        // can move a peg out if they rolled a 6 and they don't already have a peg there
        // and they have pegs in home
        if (dieRoll == 6 && relativeBoard[0] != currentPlayer && homePegs[currentPlayer.intValue] > 0) {
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

        return validMoves;
    }

    private int[] relativePositionsToAbsolute(int[] positions, PlayerColor currentPlayer) {
        int offset = currentPlayer.homePosition();
        int[] translatedPositions = new int[positions.length];
        for (int i = 0; i < positions.length; i++) {
            // don't convert finish line positions
            if (positions[i] >= normalSpaces.length)
                translatedPositions[i] = positions[i];
                // convert normal spaces
            else if (positions[i] >= 0)
                translatedPositions[i] = (positions[i] + offset) % normalSpaces.length;
                // don't convert -1 (null) or -2 (home) positions
            else
                translatedPositions[i] = positions[i];
        }

        return translatedPositions;
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

    private void updateAbsoluteBoard(PlayerColor[] relativeBoard, PlayerColor currentPlayer) {
        int offset = currentPlayer.homePosition();
        for (int i = 0; i < normalSpaces.length; i++) {
            int translatedIndex = (i + normalSpaces.length - offset) % normalSpaces.length;
            normalSpaces[i] = relativeBoard[translatedIndex];
        }

        for (int i = 0; i < PEG_COUNT; i++) {
            int translatedIndex = normalSpaces.length + i;
            finishLineSpaces[currentPlayer.intValue][i] = relativeBoard[translatedIndex];
        }
    }

    private int getSelectionNumber(int[] moveOptions, int position) {
        int selectionNumber = 1;
        for (int i = 0; i < moveOptions.length; i++) {
            if (moveOptions[i] == position)
                return selectionNumber;

            if (moveOptions[i] >= 0)
                selectionNumber++;
        }

        return selectionNumber;
    }

    private String formatPlayerSelectOptions(String boardOutput, int[] moveOptions, PlayerColor currentPlayer) {
        for (int i = 0; i < homePegs.length; i++) {
            String replaceString = String.format("[h%s]", i);
            String displayString;
            if (contains(moveOptions, -2) && currentPlayer.intValue == i)
                displayString = String.format(
                        "%s%s%s",
                        currentPlayer.openTag(),
                        "0",
                        currentPlayer.closeTag()
                );
            else
                displayString = " ";

            boardOutput = boardOutput.replace(replaceString, displayString);
        }

        for (int i = 0; i < normalSpaces.length; i++) {
            String replaceString = String.format("[S%s]", i);
            String displayString;
            if (contains(moveOptions, i))
                displayString = String.format(
                        "%s%s%s",
                        normalSpaces[i].openTag(),
                        getSelectionNumber(moveOptions, i),
                        normalSpaces[i].closeTag()
                );
            else
                displayString = " ";


            boardOutput = boardOutput.replace(replaceString, displayString);
        }

        for (int i = 0; i < finishLineSpaces.length; i++) {
            for (int j = 0; j < PEG_COUNT; j++) {
                String replaceString = String.format("[SF%s%s]", i, j);
                String displayString;
                if (contains(moveOptions, normalSpaces.length + j) && currentPlayer.intValue == i)
                    displayString = String.format(
                            "%s%s%s",
                            currentPlayer.openTag(),
                            getSelectionNumber(moveOptions, normalSpaces.length + j),
                            currentPlayer.closeTag()
                    );
                else
                    displayString = " ";


                boardOutput = boardOutput.replace(replaceString, displayString);
            }
        }

        return boardOutput;
    }

    private boolean contains(int[] haystack, int needle) {
        for (int n : haystack) {
            if (n == needle)
                return true;
        }
        return false;
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
