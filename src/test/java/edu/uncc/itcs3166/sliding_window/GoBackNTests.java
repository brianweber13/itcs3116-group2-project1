package edu.uncc.itcs3166.sliding_window;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GoBackNTests {
    GoBackNProtocol alphabetSender;
    GoBackNProtocol numberSender;

    @BeforeAll
    void init() {
        try {
            Scanner alphabetScanner = new Scanner(
                    new File("alphabet-packets.txt"));
            Scanner numberScanner = new Scanner(new File("number-packets.txt"));
            alphabetSender = new GoBackNProtocol(alphabetScanner, false);
            numberSender = new GoBackNProtocol(numberScanner, false);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testSendData() {
        String[] packets = { "a", "b", "c", "d", "e", "f" };
        alphabetSender.sendData(2, 3, packets);
        Frame expected = new Frame(null, 2, 2, "c");
        Frame received = numberSender.getFrameWork().fromPhysicalLayer();
        assertEquals(expected, received);
    }

    @Test
    void testSendBetweenInstances() {

        // Frame waitForFrameTestFrame = new Frame(frameKind.DATA, 0, 0,
        // "only used in waitForFrame() test!");
        // testFramework1.disableNetworkLayer();
        // testFramework2.toPhysicalLayer(waitForFrameTestFrame);
        // testFramework1.startTimer(0);
        // eventType result = testFramework1.waitForEvent();
        // assertEquals(eventType.FRAME_ARRIVAL, result);
    }
}
