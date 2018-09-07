package edu.uncc.itcs3166.sliding_window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

/**
 * Framework simulates the physical and network layers and can be replaced in
 * order to interface with an actual physical/network layer. We make a few
 * assumptions: - the network layer does not need to be enabled/disabled because
 * we only accept input when it is directly asked for (see the fromNetwork
 * function) This allows us to focus on implementing the data link layer
 * protocols discussed in chapter 3
 * 
 * This mockup of the network and physical layers uses command line input/output
 * as the network layer and a file on the disk as the physical layer. We assume
 * that all instances that want to communicate operate in the same directory.
 * Only two instances can communicate at a time.
 * 
 * @author Brian Weber
 */
public class Framework {
    public enum eventType {
        FRAME_ARRIVAL, CHECKSUM_ERR, TIMEOUT
    }

    final static int MAX_PKT = 1024;
    final static String PHYSICAL_LAYER_FILE = "physical-layer";
    final int MAX_SEQUENCE_NUMBER;
    final int TIMEOUT_IN_MILLIS;
    final Scanner scanner;

    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private String fileChecksum;

    /**
     * @param mAX_SEQUENCE_NUMBER
     *            the max number of frames the protocol can send in one go. In
     *            other words, the size of the window
     * @param tIMEOUT_IN_MILLIS
     *            the time to wait before timing out
     * @param scanner
     *            an input scanner, created by App.java and passed to the
     *            framework via the protocol. Typically this is constructed with
     *            `new Scanner(System.in)`. See SimplexStopAndWait, App for an
     *            example implementation
     */
    public Framework(int mAX_SEQUENCE_NUMBER, int tIMEOUT_IN_MILLIS,
            Scanner scanner) {
        super();
        MAX_SEQUENCE_NUMBER = mAX_SEQUENCE_NUMBER;
        TIMEOUT_IN_MILLIS = tIMEOUT_IN_MILLIS;
        this.scanner = scanner;
        init();
    }

    public Framework() {
        super();
        MAX_SEQUENCE_NUMBER = 1;
        TIMEOUT_IN_MILLIS = 5000;
        scanner = new Scanner(System.in);
        init();
    }

    /**
     * does set up stuff needed in all constructors
     */
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
        fileChecksum = generateChecksum(PHYSICAL_LAYER_FILE);
    }

    /**
     * generates a checksum for the given file in order to discover when the
     * file has changed
     * 
     * @param pathToFile
     * @return the generated checksum
     */
    String generateChecksum(String pathToFile) {
        String algorithm = "MD5";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(Files.readAllBytes(Paths.get(pathToFile)));
            byte[] digest = md.digest();
            String myChecksum = DatatypeConverter.printHexBinary(digest)
                    .toUpperCase();
            return myChecksum;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not find algorithm " + algorithm);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file " + pathToFile);
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Waits for an event to happen and returns the even type.
     * 
     * @return the type of event
     */
    eventType waitForEvent() {
        long now = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(201);
            } catch (InterruptedException e) {
                System.out.println("Could not sleep thread...");
                e.printStackTrace();
            }
            // check if file has changed
            String newChecksum = generateChecksum(PHYSICAL_LAYER_FILE);
            if (!newChecksum.equals(fileChecksum)) {
                fileChecksum = newChecksum;
                return eventType.FRAME_ARRIVAL;
            }

            // TODO: Implement checksumerr here
            else if (System.currentTimeMillis() > (now + TIMEOUT_IN_MILLIS)) {
                return eventType.TIMEOUT;
            }
        }
    }

    // Fetch a packet from the network layer for transmission on the channel.
    /**
     * gets a packet from the network layer (uses scanner.next() in order to get
     * the next word input on the command line). If there are no words
     * remaining, will prompt the user for more
     * 
     * @return the next 'packet' (word) to send
     */
    String fromNetworkLayer() {
        String data = scanner.next();
        return data;
    }

    /**
     * sends the frame to the 'network layer' (command line) uses
     * System.out.println()
     * 
     * @param packet
     *            the 'packet' (string) to send
     */
    void toNetworkLayer(String packet) {
        System.out.println(packet);
    }

    /**
     * gets a frame from the "physical layer" (the file on disk)
     * 
     * @return the received frame
     */
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

    /**
     * sends the frame to the 'physical layer' (the file on disk)
     * 
     * @param frameToSend
     */
    void toPhysicalLayer(Frame frameToSend) {
        try {
            writer = new ObjectOutputStream(
                    new FileOutputStream(PHYSICAL_LAYER_FILE));
            writer.writeObject(frameToSend);
            writer.close();
            fileChecksum = generateChecksum(PHYSICAL_LAYER_FILE);
        } catch (IOException ex) {
            System.out.println("Unable to open file '"
                    + System.getProperty("user.dir")
                    + System.getProperty("file.separator") + PHYSICAL_LAYER_FILE
                    + "' For writing. Do you have"
                    + " write permissions on this directory? Does this file exist?");
            ex.printStackTrace();
        }
    }

    /**
     * increments a number, but if you exceed the max value, then it will reset
     * the number to zero. This is used to keep track of which packet in the
     * window you're sending or receiving. Ideally, max should be set as a final
     * int in your implentation and then passed to every inc call.
     * 
     * typical usage is below:
     * 
     * `frameExpected = inc(frameExpected, * MAX_SEQUENCE_NUMBER)`
     * 
     * @param i
     *            number to increment
     * @param max
     *            MAX_SEQUENCE_NUMBER
     * @return the incremented number
     */
    int inc(int i, int max) {
        if (i < max)
            i++;
        else
            i = 0;
        return i;
    }

}
