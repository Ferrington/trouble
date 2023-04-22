package org.trouble;

public enum PlayerColor {
    YELLOW(0),
    BLUE(1),
    RED(2),
    GREEN(3);

    final int intValue;

    PlayerColor(int intValue) {
        this.intValue = intValue;
    }
}
