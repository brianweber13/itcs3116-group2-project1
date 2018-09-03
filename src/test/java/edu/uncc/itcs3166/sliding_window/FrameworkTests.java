package edu.uncc.itcs3166.sliding_window;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.uncc.itcs3166.sliding_window.Frame.frameKind;

public class FrameworkTests {
    Framework testFramework1 = new Framework();
    Framework testFramework2 = new Framework();

    @Test
    void sendAcrossNetworkLayer() {
        Frame testFrame = new Frame(frameKind.DATA, 0, 0,
                "a whole bunch of data");
        testFramework1.toPhysicalLayer(testFrame);
        Frame receivedFrame = testFramework2.fromPhysicalLayer();
        System.out.println(testFrame.toString());
        System.out.println(receivedFrame.toString());
        // assertTrue(testFrame.equals(receivedFrame));
        assertEquals(testFrame, receivedFrame);
    }
}
