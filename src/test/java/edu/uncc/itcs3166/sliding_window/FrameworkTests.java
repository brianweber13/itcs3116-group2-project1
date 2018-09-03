package edu.uncc.itcs3166.sliding_window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import edu.uncc.itcs3166.sliding_window.Frame.frameKind;
import edu.uncc.itcs3166.sliding_window.Framework.eventType;

public class FrameworkTests {
    Framework testFramework1 = new Framework();
    Framework testFramework2 = new Framework();
    public final Frame testFrame = new Frame(frameKind.DATA, 0, 0,
            "a whole bunch of data");

    @Test
    void sendAcrossPhysicalLayer() {
        testFramework1.toPhysicalLayer(testFrame);
        Frame receivedFrame = testFramework2.fromPhysicalLayer();
        System.out.println("Expected Frame:");
        System.out.println(testFrame.toString());
        System.out.println();
        System.out.println("Received Frame:");
        System.out.println(receivedFrame.toString());
        System.out.println();
        // assertTrue(testFrame.equals(receivedFrame));
        assertEquals(testFrame, receivedFrame);
    }

    @Test
    void waitForFrame() {
        Thread waitingFramework = new Thread() {
            public void run() {
                eventType result = testFramework1.waitForEvent();
                System.out.println("Result: " + result);
                System.out.println();
                assertEquals(eventType.FRAME_ARRIVAL, result);
            }
        };
        Thread sendingFramework = new Thread() {
            public void run() {
                testFramework2.toPhysicalLayer(testFrame);
            }
        };
        // start threads, both execute simultaneously
        waitingFramework.start();
        sendingFramework.start();

        try {
            sendingFramework.join();
            waitingFramework.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            fail("Something went wrong in our threads...");
            e.printStackTrace();
        }
    }
}
