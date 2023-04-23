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

    public void takeTurn(boolean firstTurn) {
        board.printBoard();
        printStartOfTurnMessage(firstTurn);


    }

    public void takeTurn() {
        takeTurn(false);
    }

    public void printStartOfTurnMessage(boolean firstTurn) {
        String message;
        if (firstTurn)
            message = "%s%s was randomly selected to go first.%s";
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