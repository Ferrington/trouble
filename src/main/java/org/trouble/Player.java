package org.trouble;

public class Player {
    IOHelper ioHelper;
    Board board;
    PlayerColor playerColor;
    String playerName;

    Player(PlayerColor playerColor, String playerName, IOHelper ioHelper, Board board) {
        this.playerColor = playerColor;
        this.playerName = playerName;
        this.ioHelper = ioHelper;
        this.board = board;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public int takeTurn(boolean firstTurn, boolean extraTurn) {
//        board.printBoard();
        printStartOfTurnMessage(firstTurn, extraTurn);
        int dieRoll = getDieRoll();
        board.movePlayer(playerColor, dieRoll);

        return dieRoll;
    }

    private int getDieRoll() {
        ioHelper.prompt("Press [Enter] to roll the die!");
        return board.rollDieSequence();
    }

    private void printStartOfTurnMessage(boolean firstTurn, boolean extraTurn) {
        String message;
        if (firstTurn)
            message = "%s%s was randomly selected to go first.%s";
        else if (extraTurn)
            message = "%s%s, you rolled a 6 so you get another roll!%s";
        else
            message = "%s%s, it's your turn!%s";

        String formattedMessage = String.format(
                message,
                playerColor.openTag(),
                playerName,
                playerColor.closeTag()
        );
        ioHelper.printString(formattedMessage);
    }
}