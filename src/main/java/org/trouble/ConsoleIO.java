package org.trouble;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleIO implements IOHelper {
    Scanner scan;

    ConsoleIO() {
        scan = new Scanner(System.in);
    }

    public String prompt(String message) {
        System.out.print(message + " ");
        return scan.nextLine();
    }

    public void printString(String str) {
        System.out.println(str);
    }

    public void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
            // I should probably do something here, but I don't know what yet
        }
    }
}
