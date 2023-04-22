package org.trouble;

public class Main {
    public static void main(String[] args) {
        IOHelper ioHelper = new ConsoleIO();
        Game game = new Game(ioHelper);
        game.start();
    }
}