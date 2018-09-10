package edu.uncc.itcs3166.sliding_window;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.uncc.itcs3166.sliding_window.Frame.frameKind;
import edu.uncc.itcs3166.sliding_window.Framework.eventType;

public class FrameworkTests {
    Scanner scanner = new Scanner(System.in);
    Frame testFrame1;
    Frame testFrame2;
    Framework testFramework1;
    Framework testFramework2;

    @BeforeEach
    void init() {
        Scanner scanner = new Scanner(System.in);
        testFrame1 = new Frame(frameKind.DATA, 0, 0, "a whole bunch of data");
        testFrame2 = new Frame(frameKind.DATA, 0, 0, "even more data!");
        testFramework1 = new Framework(0, 1000, scanner);
        testFramework2 = new Framework(0, 1000, scanner);
    }

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
        // testFramework1.disableNetworkLayer();
        testFramework2.toPhysicalLayer(waitForFrameTestFrame);
        testFramework1.startTimer(0);
        eventType result = testFramework1.waitForEvent();
        assertEquals(eventType.FRAME_ARRIVAL, result);
    }

    @Test
    void testTimers() {
        long now = System.currentTimeMillis();
        testFramework1.startTimer(0);
        testFramework1.startTimer(1);
        testFramework1.startTimer(2);
        testFramework1.startTimer(3);
        testFramework1.startTimer(4);
        Map<Integer, Long> timers = testFramework1.getRunningTimers();
        assertEquals(5, timers.size());
        for (Map.Entry<Integer, Long> pair : timers.entrySet()) {
            assertTrue((now + 10) > pair.getValue());
        }
        testFramework1.stopTimer(0);
        testFramework1.stopTimer(1);
        testFramework1.stopTimer(2);
        testFramework1.stopTimer(3);
        testFramework1.stopTimer(4);
        assertEquals(0, timers.size());
    }

    @Test
    void waitForTimeout() {
        // testFramework1.disableNetworkLayer();
        testFramework1.startTimer(0);
        eventType event = testFramework1.waitForEvent();
        assertEquals(eventType.TIMEOUT, event);
    }

    @Test
    void testArrayDiff() {
        String[] arrA = { "1", "2", "3", "4", "5", "25", "10" };
        String[] arrB = { "1", "2", "10", "4", "30" };
        String[] arrC = Framework.differences(arrA, arrB);

        String[] expected = { "25", "3", "30", "5" };
        assertTrue(Arrays.equals(expected, arrC));
    }

    // @Test
    // void waitForNetworkLayerReady() {
    // eventType event = testFramework1.waitForEvent();
    // assertEquals(eventType.NETWORK_LAYER_READY, event);
    // testFramework1.disableNetworkLayer();
    // testFramework1.startTimer(0);
    // event = testFramework1.waitForEvent();
    // assertEquals(eventType.TIMEOUT, event);
    // testFramework1.enableNetworkLayer();
    // event = testFramework1.waitForEvent();
    // assertEquals(eventType.NETWORK_LAYER_READY, event);
    // }
}
