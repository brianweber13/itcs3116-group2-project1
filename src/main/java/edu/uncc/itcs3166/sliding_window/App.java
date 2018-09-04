package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

/**
 * @author Brian Weber
 *
 */
public class App {
    static Scanner scanner = new Scanner(System.in);
    public static final String[] protocolList = new String[] {
            "Simplex stop-and-wait (s)" };

    public static void main(String[] args) {
        while (true) {
            System.out.println("Which protocol would you like to demonstrate?");
            for (int i = 1; i < protocolList.length + 1; i++) {
                System.out.println(i + ". " + protocolList[i - 1]);
            }
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 's':
                demoSimplexStopAndWait();
            default:
                System.out
                        .println("Protocol not recognized. Enter the letter in "
                                + "parentheses for the protocol you wish to demo.");
            }
        }
    }

    private static void demoSimplexStopAndWait() {
        while (true) {
            System.out.println(
                    "Is this instance the sender (s) or receiver (r)?");
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 's':
                SimplexStopAndWait.Sender(scanner);
            case 'r':
                SimplexStopAndWait.Receiver(scanner);
            default:
                System.out.println("Please enter 's' or 'r'");
            }
        }
    }
}
