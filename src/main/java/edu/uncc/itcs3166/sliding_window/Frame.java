package edu.uncc.itcs3166.sliding_window;

public class Frame {
    public enum frameKind {
        DATA, ACKNOWLEDGEMENT, NEGATIVE_ACKNOWLEDGEMENT
    }

    private frameKind kind;
    private int sequenceNumber;
    private int awknoledgementNumber;
    private String packet;

    public Frame() {
        super();
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

    public int getAwknoledgementNumber() {
        return awknoledgementNumber;
    }

    public void setAwknoledgementNumber(int awknoledgementNumber) {
        this.awknoledgementNumber = awknoledgementNumber;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }
}
