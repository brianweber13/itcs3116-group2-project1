package edu.uncc.itcs3166.sliding_window;

import java.util.Scanner;

import edu.uncc.itcs3166.sliding_window.Framework.eventType;

public class GoBackNProtocol {
    public static final int MAX_SEQ = 5;
    public static final int TIME_OUT_LENGTH = 5000;
    private Framework frameWork;
    private Boolean failSomePackets;

    public GoBackNProtocol(Scanner scannerForGoBackN, boolean failSomePackets) {
        super();
        frameWork = new Framework(MAX_SEQ, TIME_OUT_LENGTH, scannerForGoBackN);
        this.failSomePackets = failSomePackets;
    }

    public void sendData(int frameNum, int frameExp, String[] packets) {
        System.out.println("sending: " + packets[frameNum]);
        Frame frameToSend = new Frame();
        frameToSend.setPacket(packets[frameNum]);
        frameToSend.setSequenceNumber(frameNum);
        frameToSend
                .setAcknowledgmentNumber((frameExp + MAX_SEQ) % (MAX_SEQ + 1));
        frameWork.toPhysicalLayer(frameToSend, failSomePackets);
        frameWork.startTimer(frameNum);
    }

    public void protocol5() {
        int nextFrameToSend;
        int ackExpected;
        int frameExpected;
        Frame r;
        String[] packets = new String[MAX_SEQ + 1];
        int nBuffered;
        int i;
        eventType event;

        ackExpected = 0;
        nextFrameToSend = 0;
        frameExpected = 0;
        nBuffered = 0;

        while (true) {
            System.out.println("ack expected: " + ackExpected);
            System.out.println("next frame to send: " + nextFrameToSend);
            event = frameWork.waitForEvent();
            System.out.println(event);
            switch (event) {
            case NETWORK_LAYER_READY:
                packets[nextFrameToSend] = frameWork.fromNetworkLayer();
                nBuffered = nBuffered + 1;
                sendData(nextFrameToSend, frameExpected, packets);
                nextFrameToSend = frameWork.inc(nextFrameToSend, MAX_SEQ);
                break;

            case FRAME_ARRIVAL:
                r = frameWork.fromPhysicalLayer();
                if (r.getSequenceNumber() == frameExpected) {
                    frameWork.toNetworkLayer(r.getPacket());
                    ackExpected = frameWork.inc(ackExpected, MAX_SEQ);
                }
                while (frameWork.between(ackExpected,
                        r.getAcknowledgmentNumber(), nextFrameToSend)) {
                    nBuffered = nBuffered - 1;
                    frameWork.stopTimer(ackExpected);
                    ackExpected = frameWork.inc(ackExpected, MAX_SEQ);
                }
                break;

            case TIMEOUT:
                nextFrameToSend = ackExpected;
                for (i = 1; i <= nBuffered; i++) {
                    sendData(nextFrameToSend, frameExpected, packets);
                    ackExpected = frameWork.inc(nextFrameToSend, MAX_SEQ);
                }
                break;
            default:
                break;
            }

            if (nBuffered < MAX_SEQ) {
                frameWork.enableNetworkLayer();
            } else {
                frameWork.disableNetworkLayer();
            }
        }

    }

    public Framework getFrameWork() {
        return frameWork;
    }

}
