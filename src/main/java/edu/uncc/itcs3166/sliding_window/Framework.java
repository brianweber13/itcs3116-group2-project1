/**
 * Framework simulates the physical and network layers and can be replaced in
 * order to interface with an actual physical/network layer. We make a few
 * assumptions:
 *   - the network layer does not need to be enabled/disabled because we only
 *     accept input when it is directly asked for (see the fromNetwork function)
 * This allows us to focus on implementing the data link layer protocols
 * discussed in chapter 3
 * @author Brian Weber
 */
package edu.uncc.itcs3166.sliding_window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Framework {
    public enum eventType {
        FRAME_ARRIVAL, CHECKSUM_ERR, TIMEOUT
    }

    final static int MAX_PKT = 1024;
    final static String PHYSICAL_LAYER_FILE = "physical-layer";
    final int MAX_SEQUENCE_NUMBER;
    final int TIMEOUT_IN_MILLIS;

    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private long fileLastModifiedTime;

    /**
     * @param mAX_SEQUENCE_NUMBER
     */
    public Framework(int mAX_SEQUENCE_NUMBER, int tIMEOUT_IN_MILLIS) {
        super();
        MAX_SEQUENCE_NUMBER = mAX_SEQUENCE_NUMBER;
        TIMEOUT_IN_MILLIS = tIMEOUT_IN_MILLIS;
        init();
    }

    public Framework() {
        super();
        MAX_SEQUENCE_NUMBER = 1;
        TIMEOUT_IN_MILLIS = 5000;
        init();
    }

    void init() {
        try {
            File physicalLayer = new File(PHYSICAL_LAYER_FILE);
            // if file already exists will do nothing
            physicalLayer.createNewFile();
        } catch (IOException e) {
            System.out.println(
                    "Cannot create file for physical layer. Do you have "
                            + "write permissions on the directory "
                            + System.getProperty("user.dir") + "?");
            e.printStackTrace();
        }
        fileLastModifiedTime = new File(PHYSICAL_LAYER_FILE).lastModified();

        // create reader
        // try {
        // reader = new ObjectInputStream(new
        // FileInputStream(NETWORK_LAYER_FILE));
        // } catch (FileNotFoundException ex) {
        // System.out.println("Unable to open file '" + NETWORK_LAYER_FILE +
        // "'");
        // } catch (IOException ex) {
        // System.out.println("Error reading file '" + NETWORK_LAYER_FILE +
        // "'");
        // // Or we could just do this:
        // // ex.printStackTrace();
        // }
    }

    // Wait for an event to happen; return its type in event.
    eventType waitForEvent() {
        long now = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(201);
            } catch (InterruptedException e) {
                System.out.println("Could not sleep thread...");
                e.printStackTrace();
            }

            if (new File(PHYSICAL_LAYER_FILE)
                    .lastModified() > fileLastModifiedTime) {
                // TODO: Implement checksumerr here
                return eventType.FRAME_ARRIVAL;
            } else if (System.currentTimeMillis() > (now + TIMEOUT_IN_MILLIS)) {
                return eventType.TIMEOUT;
            }
        }
    }

    // Fetch a packet from the network layer for transmission on the channel.
    String fromNetworkLayer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the data to be sent: ");
        String data = scanner.nextLine();
        scanner.close();
        return data;
    }

    // Deliver information from an inbound frame to the network layer.
    void toNetworkLayer(String packet) {
        System.out.println(packet);
    }

    // Go get an inbound frame from the physical layer and copy it to r.
    Frame fromPhysicalLayer() {
        Frame frameFromPhysicalLayer = new Frame();
        try {
            reader = new ObjectInputStream(
                    new FileInputStream(PHYSICAL_LAYER_FILE));
            frameFromPhysicalLayer = (Frame) reader.readObject();
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '"
                    + System.getProperty("user.dir")
                    + System.getProperty("path.separator") + PHYSICAL_LAYER_FILE
                    + "' for reading. File Not Found.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '" + PHYSICAL_LAYER_FILE + "'");
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException...");
            e.printStackTrace();
        }
        return frameFromPhysicalLayer;
    }

    // PassTheFrame to the physical layer for transmission{
    void toPhysicalLayer(Frame frameToSend) {
        try {
            writer = new ObjectOutputStream(
                    new FileOutputStream(PHYSICAL_LAYER_FILE));
            writer.writeObject(frameToSend);
            fileLastModifiedTime = new File(PHYSICAL_LAYER_FILE).lastModified();
            writer.close();
        } catch (IOException ex) {
            System.out.println("Unable to open file '"
                    + System.getProperty("user.dir")
                    + System.getProperty("file.separator") + PHYSICAL_LAYER_FILE
                    + "' For writing. Do you have"
                    + " write permissions on this directory? Does this file exist?");
            ex.printStackTrace();
        }
    }

    // Start the clock running and enable the timeout event.
    // Do we need this?
    void startTime(int k) {
        // TODO: come up with better name than 'k'

    }

    // Stop the clock and disable the timeout event.
    // Do we need this?
    void stopTimer(int k) {

    }

    // Start an auxiliary timer and enable the ack timeout event.
    // Do we need this?
    void startAckTimer() {

    }

    // Stop the auxiliary timer and disable the ack timeout event.
    // Do we need this?
    void stopAckTimer() {

    }

    // Allow the network layer to cause a network layer ready event.
    // Do we need this?
    void enableNetworkLayer() {

    }

    // Forbid the network layer from causing a network layer ready event.
    // Do we need this?
    void disableNetworkLayer() {

    }

    int inc(int i, int max) {
        if (i < max)
            i++;
        else
            i = 0;
        return i;
    }

}
