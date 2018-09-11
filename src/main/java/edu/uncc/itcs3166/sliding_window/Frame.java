package edu.uncc.itcs3166.sliding_window;

import java.io.Serializable;

public class Frame implements Serializable {
    public enum frameKind {
        DATA, ACKNOWLEDGEMENT, NEGATIVE_ACKNOWLEDGEMENT
    }

    private static final long serialVersionUID = 1L;
    private frameKind kind;
    private int sequenceNumber;
    private int acknowledgmentNumber;
    private String packet;

    public Frame() {
        super();
    }

    /**
     * @param kind
     * @param sequenceNumber
     * @param acknowledgmentNumber
     * @param packet
     */
    public Frame(frameKind kind, int sequenceNumber, int acknowledgmentNumber,
            String packet) {
        super();
        this.kind = kind;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgmentNumber = acknowledgmentNumber;
        this.packet = packet;
    }

    public frameKind getKind() {
        return kind;
    }

    public void setKind(frameKind kind) {
        this.kind = kind;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAcknowledgmentNumber() {
        return acknowledgmentNumber;
    }

    public void setAcknowledgmentNumber(int acknowledgmentNumber) {
        this.acknowledgmentNumber = acknowledgmentNumber;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public String toString() {
        // String description = "### FRAME ###\n" + "Kind: " + this.kind + "\n"
        // + "Sequence Number: " + this.sequenceNumber + "\n"
        // + "Acknowledgement Number: " + this.acknowledgmentNumber + "\n"
        // + "Packet: " + this.packet;
        String description = "### FRAME ###\n" + "Sequence Number: "
                + this.sequenceNumber + "\n" + "Acknowledgement Number: "
                + this.acknowledgmentNumber + "\n" + "Packet: " + this.packet;
        return description;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null)
            return false;
        else if (obj instanceof Frame) {
            if (((Frame) obj).packet == null && this.packet == null) {
                return true;
            } else if (((Frame) obj).packet == null || this.packet == null) {
                return false;
            } else {
                Frame frameToCompare = (Frame) obj;
                if (this.kind == frameToCompare.kind
                        && this.sequenceNumber == frameToCompare.sequenceNumber
                        && this.acknowledgmentNumber == frameToCompare.acknowledgmentNumber
                        && this.packet.equals(frameToCompare.packet)) {
                    return true;
                }
            }
        }
        return false;
    }
}
