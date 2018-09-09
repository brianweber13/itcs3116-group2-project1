package edu.uncc.itcs3166.sliding_window;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import edu.uncc.itcs3166.sliding_window.Frame.frameKind;
import edu.uncc.itcs3166.sliding_window.Framework.eventType;

public class FrameworkTests {
    Scanner scanner = new Scanner(System.in);
    Frame testFrame1 = new Frame(frameKind.DATA, 0, 0, "a whole bunch of data");
    Frame testFrame2 = new Frame(frameKind.DATA, 0, 0, "even more data!");
    Framework testFramework1 = new Framework(0, 1000, scanner);
    Framework testFramework2 = new Framework(0, 1000, scanner);

    @Test
    void sendAcrossPhysicalLayer() {
        testFramework1.toPhysicalLayer(testFrame1);
        Frame receivedFrame = testFramework2.fromPhysicalLayer();
        // System.out.println("Expected Frame:");
        // System.out.println(testFrame.toString());
        // System.out.println();
        // System.out.println("Received Frame:");
        // System.out.println(receivedFrame.toString());
        // System.out.println();
        // assertTrue(testFrame.equals(receivedFrame));
        assertEquals(testFrame1, receivedFrame);
    }

    @Test
    void waitForFrame() {
        Frame waitForFrameTestFrame = new Frame(frameKind.DATA, 0, 0,
                "only used in waitForFrame() test!");
        testFramework1.disableNetworkLayer();
        testFramework2.toPhysicalLayer(waitForFrameTestFrame);
        testFramework1.startTimer(0);
        eventType result = testFramework1.waitForEvent();
        assertEquals(eventType.FRAME_ARRIVAL, result);
    }

    @Test
    void waitForTimeout() {
        testFramework1.disableNetworkLayer();
        testFramework1.startTimer(0);
        eventType event = testFramework1.waitForEvent();
        assertEquals(eventType.TIMEOUT, event);
    }

    @Test
    void waitForNetworkLayerReady() {
        eventType event = testFramework1.waitForEvent();
        assertEquals(eventType.NETWORK_LAYER_READY, event);
        testFramework1.disableNetworkLayer();
        testFramework1.startTimer(0);
        event = testFramework1.waitForEvent();
        assertEquals(eventType.TIMEOUT, event);
        testFramework1.enableNetworkLayer();
        event = testFramework1.waitForEvent();
        assertEquals(eventType.NETWORK_LAYER_READY, event);
    }
}
