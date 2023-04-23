package org.trouble;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Game {
    static private final Comparator<Player> ascColor;

    static {
        ascColor = Comparator.comparing(Player::getPlayerColor);
    }

    Random rand = new Random();
    Board board;
    IOHelper ioHelper;
    Player[] players;

    public Game(IOHelper ioHelper) {
        this.board = new Board(ioHelper);
        this.ioHelper = ioHelper;
    }

    public void start() {
        // int numberOfPlayers = getNumberOfPlayers();
        // createPlayers(numberOfPlayers);
        int numberOfPlayers = 3;
        createTestPlayers(numberOfPlayers);

        // test output
//        for (Player player : players) {
//            System.out.printf("%s - %s%n", player.getPlayerName(), player.getPlayerColor());
//        }

        int randomPlayerNumber = rand.nextInt(numberOfPlayers);
        Player firstPlayer = players[randomPlayerNumber];


        firstPlayer.takeTurn(true);

        // TODO - loop through players, calling take turn on each

        // TODO - after each turn, check if someone has won

        // TODO - if someone has won, end the game

    }

    private void createTestPlayers(int numberOfPlayers) {
        players = new Player[numberOfPlayers];

        players[0] = new Player(PlayerColor.GREEN, "Christopher", ioHelper, board);
        players[1] = new Player(PlayerColor.YELLOW, "Sam", ioHelper, board);
        players[2] = new Player(PlayerColor.RED, "Bartholomew", ioHelper, board);

        Arrays.sort(players, ascColor);
    }

    private void createPlayers(int numberOfPlayers) {
        players = new Player[numberOfPlayers];

        for (int i = 0; i < numberOfPlayers; i++) {
            String playerName = getPlayerName(i + 1);
            PlayerColor playerColor = getPlayerColor(i + 1);
            players[i] = new Player(playerColor, playerName, ioHelper, board);
        }

        Arrays.sort(players, ascColor);
    }

    private String getPlayerName(int playerNumber) {
        return ioHelper.prompt(
                String.format("Please enter a name for player %s.", playerNumber)
        );
    }

    private PlayerColor getPlayerColor(int playerNumber) {
        final String validInputs = "YyBbRrGg";
        while (true) {
            String playerInput = ioHelper.prompt(createPlayerColorPrompt(playerNumber));

            PlayerColor playerInputColor;
            if (playerInput.equalsIgnoreCase("Y"))
                playerInputColor = PlayerColor.YELLOW;
            else if (playerInput.equalsIgnoreCase("B"))
                playerInputColor = PlayerColor.BLUE;
            else if (playerInput.equalsIgnoreCase("R"))
                playerInputColor = PlayerColor.RED;
            else if (playerInput.equalsIgnoreCase("G"))
                playerInputColor = PlayerColor.GREEN;
            else
                continue;

            if (isAvailableColor(playerInputColor, playerNumber))
                return playerInputColor;
        }
    }

    private boolean isAvailableColor(PlayerColor playerColor, int playerNumber) {
        int playersCreatedCount = playerNumber - 1;

        for (int i = 0; i < playersCreatedCount; i++) {
            if (playerColor == players[i].getPlayerColor())
                return false;
        }

        return true;
    }

    private String createPlayerColorPrompt(int playerNumber) {
        int playersCreatedCount = playerNumber - 1;
        String[] optionsTemplate = new String[]{"(Y)ellow", "(B)lue", "(R)ed", "(G)reen"};
        String[] options = new String[optionsTemplate.length - playersCreatedCount];

        int optionsIndex = 0;
        for (PlayerColor color : PlayerColor.values()) {
            if (isAvailableColor(color, playerNumber)) {
                options[optionsIndex] = optionsTemplate[color.intValue];
                optionsIndex++;
            }
        }

        String prompt = String.format("Please select a color for player %s: ", playerNumber);
        for (int i = 0; i < options.length; i++) {
            if (i == options.length - 1)
                prompt += String.format("or %s.", options[i]);
            else
                prompt += String.format("%s, ", options[i]);
        }

        return prompt;
    }

    private int getNumberOfPlayers() {
        int numberOfPlayers = 0;
        while (numberOfPlayers < 2 || numberOfPlayers > 4) {
            try {
                String response = ioHelper.prompt("How many players are there? [2-4]");
                numberOfPlayers = Integer.parseInt(response);
            } catch (NumberFormatException e) {
            }
            if (numberOfPlayers < 2 || numberOfPlayers > 4)
                ioHelper.printString("Please enter a number 2-4");
        }

        return numberOfPlayers;
    }
}
