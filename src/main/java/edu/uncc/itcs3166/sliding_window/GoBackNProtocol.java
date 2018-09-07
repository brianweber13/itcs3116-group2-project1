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
        String[] packet = new String[MAX_SEQ + 1];
        int nBuffered;
        int i;
        eventType event;

        ackExpected = 0;
        nextFrameToSend = 0;
        frameExpected = 0;
        nBuffered = 0;

        while (true) {
            event = frameWork.waitForEvent();
            switch (event) {
            case NETWORK_LAYER_READY:
                packet[nextFrameToSend] = frameWork.fromNetworkLayer();
                nBuffered = nBuffered + 1;
                sendData(nextFrameToSend, frameExpected, packet);
                frameWork.inc(nextFrameToSend, MAX_SEQ);

            case FRAME_ARRIVAL:
                r = frameWork.fromPhysicalLayer();
                if (r.getSequenceNumber() == frameExpected) {
                    frameWork.toNetworkLayer(r.getPacket());
                    frameWork.inc(ackExpected, MAX_SEQ);
                }
                while (frameWork.between(ackExpected,
                        r.getAcknowledgmentNumber(), nextFrameToSend)) {
                    nBuffered = nBuffered - 1;
                    frameWork.stopTimer(ackExpected);
                    frameWork.inc(ackExpected, MAX_SEQ);
                }
                break;

            case TIMEOUT:
                nextFrameToSend = ackExpected;
                for (i = 1; i <= nBuffered; i++) {
                    sendData(nextFrameToSend, frameExpected, packet);
                    frameWork.inc(nextFrameToSend, MAX_SEQ);
                }
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

}
