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
}
