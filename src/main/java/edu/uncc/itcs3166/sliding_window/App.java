package edu.uncc.itcs3166.sliding_window;

import java.io.File;
import java.io.FileNotFoundException;
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
        // try {
        // File tmp = new File("tmp");
        // tmp.createNewFile();
        // System.out.println(tmp.getPath());
        // System.out.println(tmp.getAbsolutePath());
        // System.out.println(tmp.getCanonicalPath());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
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
        Scanner packetInput = new Scanner("alpha beta charlie delta echo");
        String packetInputFileName = "";
        boolean gotInfo = false;
        while (!gotInfo) {
            System.out.println("Send letters (l) or numbers (n)?");
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 'l':
                packetInputFileName = "alphabet-packets.txt";
                gotInfo = true;
                break;
            case 'n':
                packetInputFileName = "number-packets.txt";
                gotInfo = true;
                break;
            default:
                System.out.println("Please enter 'l' or 'n'");
            }
        }
        try {
            packetInput = new Scanner(new File(packetInputFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        gotInfo = false;
        boolean failPackets = false;
        while (!gotInfo) {
            System.out.println("Would you like some packets to fail? (y/n)");
            String input = scanner.nextLine();
            switch (Character.toLowerCase(input.charAt(0))) {
            case 'y':
                failPackets = true;
                break;
            case 'n':
                break;
            default:
                System.out.println("Please enter 'y' or 'n'");
            }
            GoBackNProtocol p = new GoBackNProtocol(packetInput, failPackets);
            // sleep to allow time for user to launch two instances and have
            // GoBackNProtocol.init() functions complete before continuing.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Could not sleep thread...");
                e.printStackTrace();
            }
            p.protocol5();
        }

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
