package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

/**
 * waits for acknowledgment before sending another frame. "Never" times out
 * 
 * @author Brian Weber
 *
 */
public class SimplexStopAndWait {
    public static void Sender(Scanner scannerForFramework) {
        Frame outboundFrame = new Frame();
        String packetToSend;
        Framework sender = new Framework(0, Integer.MAX_VALUE,
                scannerForFramework);

        while (true) {
            packetToSend = sender.fromNetworkLayer();
            System.out.println(
                    "Got some data fromt the network layer! Sending it...");
            outboundFrame.setPacket(packetToSend);
            sender.toPhysicalLayer(outboundFrame);
            System.out.println("Waiting for acknowledgment...");
            sender.waitForEvent(); // will 'always' be FRAME_ARRIVAL (unless
                                   // the timeout DOES in fact occur, which is
                                   // incredibly unlikely given how long it
                                   // is.)
            System.out.println("Acknowledgment received! Continuing...");
            System.out.println();
        }
    }

    public static void Receiver(Scanner scannerForFramework) {
        Frame inboundFrame, outboundFrame = new Frame();
        Framework receiver = new Framework(0, Integer.MAX_VALUE,
                scannerForFramework);

        while (true) {
            System.out.println("Ready to receive!");
            receiver.waitForEvent(); // will 'always' be FRAME_ARRIVAL (unless
                                     // the timeout DOES in fact occur, which is
                                     // incredibly unlikely given how long it
                                     // is.)
            System.out.println("Received a packet! Here it is: ");
            inboundFrame = receiver.fromPhysicalLayer();
            receiver.toNetworkLayer(inboundFrame.getPacket());
            System.out.println("Sending acknowledgment...");
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // System.out.println("sleep interrupted");
            // e.printStackTrace();
            // }
            receiver.toPhysicalLayer(outboundFrame);
            System.out.println();
        }
    }
}
