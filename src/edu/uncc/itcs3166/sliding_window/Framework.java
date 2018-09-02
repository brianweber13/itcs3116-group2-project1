/**
 * 
 */
package edu.uncc.itcs3166.sliding_window;

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
    final int MAX_SEQUENCE_NUMBER;

    /**
     * @param mAX_SEQUENCE_NUMBER
     */
    public Framework(int mAX_SEQUENCE_NUMBER) {
        super();
        MAX_SEQUENCE_NUMBER = mAX_SEQUENCE_NUMBER;
    }

    public Framework() {
        super();
        MAX_SEQUENCE_NUMBER = 1;
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
        return new Frame();
    }

    // PassTheFrame to the physical layer for transmission{
    void toPhysicalLayer(Frame frameToSend) {

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
