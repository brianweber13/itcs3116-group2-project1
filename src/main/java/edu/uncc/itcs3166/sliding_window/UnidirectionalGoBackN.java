package edu.uncc.itcs3166.sliding_window;

import java.util.LinkedList;
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
        System.out.println("sending packet: " + packets[frameNum]);
        Frame frameToSend = new Frame();
        frameToSend.setPacket(packets[frameNum]);
        frameToSend.setSequenceNumber(frameNum);
        sendingFramework.toPhysicalLayer(frameToSend, failSomePackets);
        sendingFramework.startTimer(frameNum);
    }

    void GoBackNSender() {
        int nextFrameToSend = 0;
        int expectedAcknowledgment = 0;
        Frame bufferFrame;
        String[] packets = new String[MAX_SEQUENCE_NUMBER + 1];
        eventType event;

        while (true) {
            while (nextFrameToSend <= MAX_SEQUENCE_NUMBER) {
                packets[nextFrameToSend] = sendingFramework.fromNetworkLayer();
                sendData(nextFrameToSend, packets);
                nextFrameToSend = sendingFramework.inc(nextFrameToSend);
            }

            event = sendingFramework.waitForEvent();
            // System.out.println("got event: " + event);
            switch (event) {
            case FRAME_ARRIVAL:
                // this is the sender: Only frames that arrive will be
                // acknowledgments

                bufferFrame = sendingFramework.fromPhysicalLayer();

                // getting n acknowledged implies that n-1, n-2, etc. are
                // also received
                while (!bufferFrame.equals(new Frame())) {

                    bufferFrame = receivingFramework.fromPhysicalLayer();
                    while (expectedAcknowledgment <= bufferFrame
                            .getAcknowledgmentNumber()) {
                        sendingFramework.stopTimer(expectedAcknowledgment);
                        expectedAcknowledgment = sendingFramework
                                .inc(expectedAcknowledgment);
                    }
                }
                break;
            case TIMEOUT:
                System.out.println("Timeout!");
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
        LinkedList<Frame> bufferFrameList = new LinkedList<Frame>();

        while (true) {
            // only outputs received frames (if they're in order) and transmits
            // acknowledgments for those received frames. Does not track what
            // acknowledgments were received by the other party
            receivingFramework.waitForEvent();

            // only event possibility is frame_arrival. no timeouts are set (no
            // calls to startTimer())
            bufferFrame = receivingFramework.fromPhysicalLayer();
            // fromPhysicalLayer returns new Frame() if there's nothing new
            while (!bufferFrame.equals(new Frame())) {
                bufferFrameList.add(bufferFrame);
                bufferFrame = receivingFramework.fromPhysicalLayer();
            }
            int i = 0;
            while (bufferFrameList.size() > 0 && i < bufferFrameList.size()) {
                if (bufferFrameList.get(i)
                        .getSequenceNumber() == expectedFrame) {
                    receivingFramework
                            .toNetworkLayer(bufferFrameList.get(i).getPacket());
                    Frame outboundAck = new Frame(null, 0, expectedFrame, null);
                    System.out.println(
                            "sending acknowledgment: " + expectedFrame);
                    receivingFramework.toPhysicalLayer(outboundAck);
                    expectedFrame = receivingFramework.inc(expectedFrame);
                    bufferFrameList.remove(i);
                    i = 0;
                } else {
                    i++;
                }
            }

        }
    }
}
