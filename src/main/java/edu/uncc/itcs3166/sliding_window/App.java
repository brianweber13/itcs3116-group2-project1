package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

/**
 * @author Brian Weber
 *
 */
public class App {
    static Scanner scanner = new Scanner(System.in);
    public static final String[] protocolList = new String[] {
            "Simplex stop-and-wait (s)", "Go back N (n)" };

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
            case 'n':
                demoGoBackN();
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
                SimplexStopAndWait.Sender(scanner, askIfVerboseMode());
            case 'r':
                SimplexStopAndWait.Receiver(scanner, askIfVerboseMode());
            default:
                System.out.println("Please enter 's' or 'r'");
            }
        }
    }

    private static void demoGoBackN() {
        // boolean gotInfo = false;
        boolean failPackets = false;
        int maxSequenceNumber = 5;
        int timeoutInMillis = 5000;
        UnidirectionalGoBackN p;

        // while (!gotInfo) {
        // System.out.println("Would you like some packets to fail? (y/n)");
        // String input = scanner.nextLine();
        // switch (Character.toLowerCase(input.charAt(0))) {
        // case 'y':
        // failPackets = true;
        // gotInfo = true;
        // break;
        // case 'n':
        // gotInfo = true;
        // break;
        // default:
        // System.out.println("Please enter 'y' or 'n'");
        // }
        // }

        p = new UnidirectionalGoBackN(maxSequenceNumber, timeoutInMillis,
                failPackets, scanner);

        while (true) {
            System.out.println(
                    "Is this instance the sender (s) or receiver (r)?");
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 's':
                p.GoBackNSender();
            case 'r':
                p.GoBackNReceiver();
            default:
                System.out.println("Please enter 's' or 'r'");
            }
        }

        // for bidirectional go-back-n: no longer used
        // boolean failPackets = false;
        // while (true) {
        // System.out.println("Would you like some packets to fail? (y/n)");
        // String input = scanner.nextLine();
        // switch (Character.toLowerCase(input.charAt(0))) {
        // case 'y':
        // failPackets = true;
        // break;
        // case 'n':
        // break;
        // default:
        // System.out.println("Please enter 'y' or 'n'");
        // }
        // GoBackNProtocol p = new GoBackNProtocol(scanner, failPackets);
        // p.protocol5();
        // }
    }

    private static boolean askIfVerboseMode() {
        while (true) {
            System.out.println("would you like to enable verbose mode? (y/n)");
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 'y':
                return true;
            case 'n':
                return false;
            default:
                System.out.println("Please enter 'y' or 'n'");
            }
        }
    }
}
