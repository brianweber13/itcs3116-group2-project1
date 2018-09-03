/**
 * 
 */
package edu.uncc.itcs3166.sliding_window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Framework provides functions to interface with a mock network layer and
 * physical layer in order to focus on implementing the protocols of the data
 * link layer, as discussed in chapter 3.
 * 
 * @author brian
 */
public class Framework {
    public enum eventType {
        FRAME_ARRIVAL, CHECKSUM_ERR, TIMEOUT
    }

    final static int MAX_PKT = 1024;
    final static String PHYSICAL_LAYER_FILE = "physical-layer";
    final int MAX_SEQUENCE_NUMBER;

    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private long fileLastModifiedTime;

    /**
     * @param mAX_SEQUENCE_NUMBER
     */
    public Framework(int mAX_SEQUENCE_NUMBER) {
        super();
        MAX_SEQUENCE_NUMBER = mAX_SEQUENCE_NUMBER;
        init();
    }

    public Framework() {
        super();
        MAX_SEQUENCE_NUMBER = 1;
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

    // ALL FUNCTIONS JUST RETURN JUNK DATA FOR NOW
    // Wait for an event to happen; return its type in event.
    eventType waitForEvent() {
        return eventType.TIMEOUT;
    }

    // Fetch a packet from the network layer for transmission on the channel.
    String fromNetworkLayer() {
        return "";
    }

    // Deliver information from an inbound frame to the network layer.
    void toNetworkLayer(String packet) {

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
    void startTime(int k) {
        // TODO: come up with better name than 'k'

    }

    // Stop the clock and disable the timeout event.
    void stopTimer(int k) {

    }

    // Start an auxiliary timer and enable the ack timeout event.
    void startAckTimer() {

    }

    // Stop the auxiliary timer and disable the ack timeout event.
    void stopAckTimer() {

    }

    // Allow the network layer to cause a network layer ready event.
    void enableNetworkLayer() {

    }

    // Forbid the network layer from causing a network layer ready event.
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
