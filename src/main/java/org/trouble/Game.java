package org.trouble;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Game {
    static private final Comparator<Player> ascColor;

    static {
        ascColor = Comparator.comparing(Player::getPlayerColor);
    }

    final String Y;
    final String END_Y;
    final String B;
    final String END_B;
    final String R;
    final String END_R;
    final String G;
    final String END_G;

    Random rand = new Random();
    Board board;
    IOHelper ioHelper;
    Player[] players;

    public Game(IOHelper ioHelper) {
        this.board = new Board(ioHelper);
        this.ioHelper = ioHelper;

        this.Y = PlayerColor.YELLOW.openTag();
        this.END_Y = PlayerColor.YELLOW.closeTag();
        this.B = PlayerColor.BLUE.openTag();
        this.END_B = PlayerColor.BLUE.closeTag();
        this.R = PlayerColor.RED.openTag();
        this.END_R = PlayerColor.RED.closeTag();
        this.G = PlayerColor.GREEN.openTag();
        this.END_G = PlayerColor.GREEN.closeTag();
    }

    public void start() {
        printWelcomeMessage();
        int numberOfPlayers = getNumberOfPlayers();
        createPlayers(numberOfPlayers);
//        int numberOfPlayers = 3;
//        createTestPlayers(numberOfPlayers);

        int currentTurn = rand.nextInt(numberOfPlayers);
        boolean isFirstTurn = true;
        boolean isExtraTurn = false;
        while (true) {
            int dieRoll = players[currentTurn].takeTurn(isFirstTurn, isExtraTurn);
            isExtraTurn = dieRoll == 6;
            if (!isExtraTurn)
                currentTurn = (currentTurn + 1) % numberOfPlayers;

            isFirstTurn = false;
            // TODO - after each turn, check if someone has won
            PlayerColor winner = board.whoWon();
            if (winner != null) {
                ioHelper.clearConsole();
                printVictoryScreen(winner);
                break;
            }

            // TODO - if someone has won, end the game
        }
    }

    private void printVictoryScreen(PlayerColor winner) {
        Player winningPlayer = null;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getPlayerColor() == winner) {
                winningPlayer = players[i];
                break;
            }
        }

        // @formatter:off
        String splash =
            " ____  ____                 ____      ____  _            _  \n" +
            "|_  _||_  _|               |_  _|    |_  _|(_)          | | \n" +
            "  \\ \\  / / .--.   __   _     \\ \\  /\\  / /  __   _ .--.  | | \n" +
            "   \\ \\/ // .'`\\ \\[  | | |     \\ \\/  \\/ /  [  | [ `.-. | | | \n" +
            "   _|  |_| \\__. | | \\_/ |,     \\  /\\  /    | |  | | | | |_| \n" +
            "  |______|'.__.'  '.__.'_/      \\/  \\/    [___][___||__](_) \n";

        String splashWithColor = winner.openTag() + splash + winner.closeTag();
        ioHelper.printString(splashWithColor);
        // @formatter:on

        String message = String.format("Congratulations, %s. You did it!", winningPlayer.getPlayerName());
        String messageWithColor = winner.openTag() + message + winner.closeTag();
        ioHelper.printString(messageWithColor);
    }

    private void printWelcomeMessage() {
        ioHelper.clearConsole();


        // @formatter:off
        String message =
            String.format(" %s_________                          __%s       %s__%s\n",G,END_G,Y,END_Y) +
            String.format("%s|  _   _  |                        [  |%s     %s[  |%s\n",G,END_G,Y,END_Y) +
            String.format("%s|_/ | | \\_|%s%s_ .--.%s   %s.--.%s   %s__   _%s   %s| |.--.%s  %s| |%s %s.---.%s\n",G,END_G,Y,END_Y,B,END_B,R,END_R,G,END_G,Y,END_Y,B,END_B) +
            String.format("    %s| |%s   %s[ `/'`\\]%s%s/ .'`\\ \\%s%s[  | | |%s  %s| '/'`\\ \\%s%s| |%s%s/ /__\\%s\n",G,END_G,Y,END_Y,B,END_B,R,END_R,G,END_G,Y,END_Y,B,END_B) +
            String.format("   %s_| |_%s   %s| |%s    %s| \\__. |%s %s| \\_/ |,%s %s|  \\__/ |%s%s| |%s%s| \\__.,%s\n",G,END_G,Y,END_Y,B,END_B,R,END_R,G,END_G,Y,END_Y,B,END_B) +
            String.format("  %s|_____|%s %s[___]%s    %s'.__.'%s  %s'.__.'_/%s%s[__;.__.'%s%s[___]%s%s'.__.'%s\n",G,END_G,Y,END_Y,B,END_B,R,END_R,G,END_G,Y,END_Y,B,END_B);
        // @formatter:on
        ioHelper.printString(message);
    }

    private void createTestPlayers(int numberOfPlayers) {
        players = new Player[numberOfPlayers];

        players[0] = new Player(PlayerColor.BLUE, "Christopher", ioHelper, board);
        players[1] = new Player(PlayerColor.GREEN, "Sam", ioHelper, board);
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
        String[] optionsTemplate = new String[]{
                String.format("%s(Y)ellow%s", Y, END_Y),
                String.format("%s(B)lue%s", B, END_B),
                String.format("%s(R)ed%s", R, END_R),
                String.format("%s(G)reen%s", G, END_G),
        };
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
