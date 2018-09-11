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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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

    private File physicalLayerFile = new File(PHYSICAL_LAYER_FILE);
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private String fileChecksum;
    private Map<Integer, Long> runningTimers = new HashMap<Integer, Long>();
    private HashSet<File> knownFiles;
    private HashSet<File> newFiles;

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
        if (!physicalLayerFile.isDirectory()) {
            if (physicalLayerFile.exists()) {
                physicalLayerFile.delete();
            }
            physicalLayerFile.mkdir();
        }
        if (physicalLayerFile.list().length > 0) {
            File[] entries = physicalLayerFile.listFiles();
            for (File s : entries) {
                s.delete();
            }
        }
        knownFiles = new HashSet();
        // File physicalLayer = new File(PHYSICAL_LAYER_FILE);
        // if file already exists will do nothing
        // physicalLayer.createNewFile();
        // fileChecksum = generateChecksum(PHYSICAL_LAYER_FILE);
    }

    public Map<Integer, Long> getRunningTimers() {
        return runningTimers;
    }

    private Set<File> difference(final Set<File> set1, final Set<File> set2) {
        final Set<File> larger = set1.size() > set2.size() ? set1 : set2;
        final Set<File> smaller = larger.equals(set1) ? set2 : set1;
        return larger.stream().filter(n -> !smaller.contains(n))
                .collect(Collectors.toSet());
    }

    // public static File[] differences(File[] first, File[] second) {
    // File[] sortedFirst = Arrays.copyOf(first, first.length); // O(n)
    // File[] sortedSecond = Arrays.copyOf(second, second.length); // O(m)
    // Arrays.sort(sortedFirst); // O(n log n)
    // Arrays.sort(sortedSecond); // O(m log m)
    //
    // int firstIndex = 0;
    // int secondIndex = 0;
    //
    // LinkedList<File> diffs = new LinkedList<File>();
    //
    // while (firstIndex < sortedFirst.length
    // && secondIndex < sortedSecond.length) { // O(n + m)
    // int compare = (int) Math.signum(sortedFirst[firstIndex]
    // .compareTo(sortedSecond[secondIndex]));
    //
    // switch (compare) {
    // case -1:
    // diffs.add(sortedFirst[firstIndex]);
    // firstIndex++;
    // break;
    // case 1:
    // diffs.add(sortedSecond[secondIndex]);
    // secondIndex++;
    // break;
    // default:
    // firstIndex++;
    // secondIndex++;
    // }
    // }
    //
    // if (firstIndex < sortedFirst.length) {
    // append(diffs, sortedFirst, firstIndex);
    // } else if (secondIndex < sortedSecond.length) {
    // append(diffs, sortedSecond, secondIndex);
    // }
    //
    // File[] strDups = new File[diffs.size()];
    //
    // return diffs.toArray(strDups);
    // }
    //
    // private static void append(LinkedList<File> diffs, File[] sortedArray,
    // int index) {
    // while (index < sortedArray.length) {
    // diffs.add(sortedArray[index]);
    // index++;
    // }
    // }
    //
    // public void removeElement(Object[] arr, int removedIdx) {
    // System.arraycopy(arr, removedIdx + 1, arr, removedIdx,
    // arr.length - 1 - removedIdx);
    // }

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
        while (true) {
            // try {
            // Thread.sleep(201);
            // } catch (InterruptedException e) {
            // System.out.println("Could not sleep thread...");
            // e.printStackTrace();
            // }

            // check if file has changed
            // String newChecksum = generateChecksum(PHYSICAL_LAYER_FILE);
            HashSet<File> tmpFiles = new HashSet<File>(
                    Arrays.asList(physicalLayerFile.listFiles()));
            // if (!newChecksum.equals(fileChecksum)) {
            if (!tmpFiles.equals(knownFiles)) {
                // fileChecksum = newChecksum;
                newFiles = new HashSet(difference(tmpFiles, knownFiles));
                knownFiles.addAll(newFiles);
                return eventType.FRAME_ARRIVAL;
            } else if (runningTimers.size() > 0) {
                for (Map.Entry<Integer, Long> pair : runningTimers.entrySet()) {
                    if (System.currentTimeMillis() > (pair.getValue()
                            + TIMEOUT_IN_MILLIS)) {
                        return eventType.TIMEOUT;
                    }
                }
            } // TODO: Implement checksumerr here
        }
    }

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
        File inputFile = null;

        // System.out.println("newFiles size: " + newFiles.size());
        if (newFiles.size() == 0) {
            return new Frame();
        } else {
            Iterator<File> it = newFiles.iterator();
            inputFile = it.next();
            newFiles.remove(inputFile);
            // Boolean result = newFiles.remove(inputFile);
            // System.out.println("Remove succeded? " + result);
            // System.out.println("new files: " + newFiles);
        }

        try {
            reader = new ObjectInputStream(new FileInputStream(inputFile));
            frameFromPhysicalLayer = (Frame) reader.readObject();
            // System.out.println("from physical: " + frameFromPhysicalLayer);
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + inputFile.getAbsolutePath()
                            + "' for reading. File may not exist?");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '" + inputFile.getAbsolutePath() + "'");
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException...");
            e.printStackTrace();
        }
        // System.out.println("receiving from physical layer: "
        // + frameFromPhysicalLayer.toString());
        return frameFromPhysicalLayer;
    }

    /**
     * sends the frame to the 'physical layer' (the file on disk)
     * 
     * @param frameToSend
     */
    void toPhysicalLayer(Frame frameToSend) {
        // System.out.println(
        // "sending to physical layer: " + frameToSend.toString());
        File outputFile = null;
        try {
            outputFile = File.createTempFile(PHYSICAL_LAYER_FILE, null,
                    physicalLayerFile);
        } catch (IOException e) {
            System.out.println("unable to create tmp file in directory "
                    + physicalLayerFile.getAbsolutePath());
            e.printStackTrace();
        }
        try {
            writer = new ObjectOutputStream(new FileOutputStream(outputFile));
            writer.writeObject(frameToSend);
            // System.out.println("to physical: " + frameToSend);
            writer.close();
            // fileChecksum = generateChecksum(PHYSICAL_LAYER_FILE);

            // HashSet<File> files = new HashSet<File>(
            // Arrays.asList(physicalLayerFile.listFiles()));
            // knownFiles.addAll(files);
            knownFiles.add(outputFile);
        } catch (IOException ex) {
            System.out.println(
                    "Unable to open file '" + outputFile.getAbsolutePath()
                            + "' For writing. Do you have"
                            + " write permissions on this directory?");
            ex.printStackTrace();
        }
    }

    void toPhysicalLayer(Frame frameToSend, boolean failMode) {
        if (failMode) {
            Random rand = new Random();
            int succeed = rand.nextInt(3);
            if (succeed != 0) {
                toPhysicalLayer(frameToSend);
            } else {
                System.out.println("The following frame was lost: "
                        + frameToSend.toString());
            }
        } else {
            toPhysicalLayer(frameToSend);
        }
    }

    void startTimer(int sequenceNumber) {
        runningTimers.put(sequenceNumber, System.currentTimeMillis());
    }

    void stopTimer(int sequenceNumber) {
        runningTimers.remove(sequenceNumber);
    }

    /**
     * increments a number, but if you exceed the max value, then it will reset
     * the number to zero. This is used to keep track of which packet in the
     * window you're sending or receiving. Ideally, max should be set as a final
     * int in your implementation and then passed to every inc call.
     * 
     * typical usage is below:
     * 
     * `frameExpected = inc(frameExpected, * MAX_SEQUENCE_NUMBER)`
     * 
     * @param i
     *            number to increment
     * @return the incremented number
     */
    int inc(int i) {
        if (i < MAX_SEQUENCE_NUMBER)
            i++;
        else
            i = 0;
        return i;
    }

    boolean between(int a, int b, int c) {
        // return true if a<=b<c circularly, false otherwise
        if (((a <= b) && (b < c)) || ((c < a) && (a <= b))
                || ((b < c) && (c < a))) {
            return true;
        } else {
            return false;
        }
    }
}
