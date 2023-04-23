package org.trouble;

public enum PlayerColor {
    YELLOW(0, "\033[0;33m"),
    BLUE(1, "\033[0;34m"),
    RED(2, "\033[0;31m"),
    GREEN(3, "\033[0;32m");

    final int intValue;
    private final String COLOR;
    private final String RESET = "\033[0m";

    PlayerColor(int intValue, String COLOR) {
        this.intValue = intValue;
        this.COLOR = COLOR;
    }

    String openTag() {
        return COLOR;
    }

    String closeTag() {
        return RESET;
    }

    int homePosition() {
        return intValue * 7;
    }
}
