package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

import edu.uncc.itcs3166.sliding_window.Framework.eventType;

public class UnidirectionalGoBackN {
    private final int MAX_SEQUENCE_NUMBER;
    private final int TIMEOUT_IN_MILLIS;
    private Boolean failSomePackets;
    private Framework sendingFramework;
    private Framework receivingFramework;

    /**
     * @param mAX_SEQUENCE_NUMBER
     *            protocol may transmit up to MAX SEQUENCE NUMBER frames without
     *            waiting for ack.
     * @param tIMEOUT_IN_MILLIS
     * @param failSomePackets
     */
    public UnidirectionalGoBackN(int mAX_SEQUENCE_NUMBER, int tIMEOUT_IN_MILLIS,
            Boolean failSomePackets, Scanner scanner) {
        super();
        MAX_SEQUENCE_NUMBER = mAX_SEQUENCE_NUMBER;
        TIMEOUT_IN_MILLIS = tIMEOUT_IN_MILLIS;
        this.failSomePackets = failSomePackets;
        this.sendingFramework = new Framework(mAX_SEQUENCE_NUMBER,
                tIMEOUT_IN_MILLIS, scanner);
        this.receivingFramework = new Framework(mAX_SEQUENCE_NUMBER,
                tIMEOUT_IN_MILLIS, scanner);
    }

    public void sendData(int frameNum, String[] packets) {
        // System.out.println("sending: " + packets[frameNum]);
        Frame frameToSend = new Frame();
        frameToSend.setPacket(packets[frameNum]);
        frameToSend.setSequenceNumber(frameNum);
        sendingFramework.toPhysicalLayer(frameToSend, failSomePackets);
        sendingFramework.startTimer(frameNum);
        System.out.println("sending frame: " + frameToSend.toString());
    }

    void GoBackNSender() {
        int nextFrameToSend = 0;
        int expectedAcknowledgment = 0;
        Frame bufferFrame;
        String[] packets = new String[MAX_SEQUENCE_NUMBER];
        eventType event;

        while (true) {
            // for (int i = 0; i < MAX_SEQUENCE_NUMBER; i++) {
            while (nextFrameToSend < MAX_SEQUENCE_NUMBER) {
                try {
                    Thread.sleep(201);
                } catch (InterruptedException e) {
                    System.out.println("Could not sleep thread...");
                    e.printStackTrace();
                }
                packets[nextFrameToSend] = sendingFramework.fromNetworkLayer();
                sendData(nextFrameToSend, packets);
                nextFrameToSend = sendingFramework.inc(nextFrameToSend);
            }
            try {
                Thread.sleep(201);
            } catch (InterruptedException e) {
                System.out.println("Could not sleep thread...");
                e.printStackTrace();
            }

            event = sendingFramework.waitForEvent();
            System.out.println("got event: " + event);
            switch (event) {
            case FRAME_ARRIVAL:
                // this is the sender: Only frames that arrive will be
                // acknowledgments

                bufferFrame = sendingFramework.fromPhysicalLayer();
                // if (bufferFrame
                // .getAcknowledgmentNumber() == expectedAcknowledgment) {
                // framework.stopTimer(expectedAcknowledgment);
                // expectedAcknowledgment = framework
                // .inc(expectedAcknowledgment);
                // } else if (bufferFrame
                // .getAcknowledgmentNumber() > expectedAcknowledgment) {

                // getting n acknowledged implies that n-1, n-2, etc. are
                // also received
                while (expectedAcknowledgment <= bufferFrame
                        .getAcknowledgmentNumber()) {
                    sendingFramework.stopTimer(expectedAcknowledgment);
                    expectedAcknowledgment = sendingFramework
                            .inc(expectedAcknowledgment);
                }
                break;
            case TIMEOUT:
                nextFrameToSend = expectedAcknowledgment;
                break;
            default:
                break;
            }
        }
    }

    void GoBackNReceiver() {
        int expectedFrame = 0;
        Frame bufferFrame;

        while (true) {
            // only outputs received frames (if they're in order) and transmits
            // acknowledgments for those received frames. Does not track what
            // acknowledgments were received by the other party
            // receivingFramework.waitForEvent();
            eventType event = receivingFramework.waitForEvent();
            System.out.println("got event: " + event);
            // only event possibility is frame_arrival. no timeouts are set (no
            // calls to startTimer())
            bufferFrame = receivingFramework.fromPhysicalLayer();
            if (bufferFrame.getSequenceNumber() == expectedFrame) {
                receivingFramework.toNetworkLayer(bufferFrame.getPacket());
                expectedFrame = receivingFramework.inc(expectedFrame);
                System.out.println("sending frame...");
            }

        }
    }
}
