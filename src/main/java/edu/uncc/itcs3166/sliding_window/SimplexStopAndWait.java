package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

/**
 * waits for acknowledgment before sending another frame. "Never" times out
 * 
 * @author Brian Weber
 *
 */
public class SimplexStopAndWait {
    public static void Sender(Scanner scannerForFramework, boolean verbose) {
        Frame outboundFrame = new Frame();
        String packetToSend;
        Framework sender = new Framework(0, Integer.MAX_VALUE,
                scannerForFramework);

        System.out.println("In our case, packets are represented by words "
                + "separated by whitespace.");
        System.out.print("Enter the data to be sent: ");
        while (true) {
            if (verbose) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("something interrupted our pause...");
                    e.printStackTrace();
                }
            }
            packetToSend = sender.fromNetworkLayer();
            if (verbose) {
                System.out.println(
                        "Got some data from the network layer! Sending it...");
            }
            outboundFrame.setPacket(packetToSend);
            sender.toPhysicalLayer(outboundFrame);
            if (verbose) {
                System.out.println("Waiting for acknowledgment...");
            }
            sender.waitForEvent(); // will 'always' be FRAME_ARRIVAL (unless
                                   // the timeout DOES in fact occur, which is
                                   // incredibly unlikely given how long it
                                   // is.)
            if (verbose) {
                System.out.println("Acknowledgment received! Continuing...");
            }
            if (verbose) {
                System.out.println();
            }
        }
    }

    public static void Receiver(Scanner scannerForFramework, boolean verbose) {
        Frame inboundFrame, outboundFrame = new Frame();
        Framework receiver = new Framework(0, Integer.MAX_VALUE,
                scannerForFramework);

        if (!verbose) {
            System.out.println("Ready to receive!");
        }
        while (true) {
            if (verbose) {
                System.out.println("Ready to receive!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("something interrupted our pause...");
                    e.printStackTrace();
                }
            }
            receiver.waitForEvent(); // will 'always' be FRAME_ARRIVAL (unless
                                     // the timeout DOES in fact occur, which is
                                     // incredibly unlikely given how long it
                                     // is.)
            if (verbose) {
                System.out.println("Received a packet! Here it is: ");
            }
            inboundFrame = receiver.fromPhysicalLayer();
            receiver.toNetworkLayer(inboundFrame.getPacket());
            if (verbose) {
                System.out.println("Sending acknowledgment...");
            }
            receiver.toPhysicalLayer(outboundFrame);
            if (verbose) {
                System.out.println();
            }
        }
    }
}
