package se.rhel.packet;

import se.rhel.Snaek;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public abstract class Packet {

    // The base packet-size
    private int PACKET_SIZE = 0;
    private int BYTE = 8;

    private ByteBuffer mBuffer;
    private byte[] mData;
    private byte mPacketId;

    private boolean mIsReady = false;

    // Where the data will reside until "compressed"
    private ByteBuffer mMaxBuffer = ByteBuffer.allocate(Snaek.PACKAGE_SIZE);

    public Packet() {}

    /**
     * Used when creating packet from input data
     * @param data
     */
    public Packet(byte[] data) {
        mBuffer = ByteBuffer.wrap(data);
        mPacketId = mBuffer.get();
    }

    public Packet(Class<?> classType) {
        mPacketId = (byte)PacketManager.getInstance().getPacketId(classType);
        putByte(mPacketId);
    }

    public byte getPacketId() {
        return mPacketId;
    }

    public byte[] getData() {
        if(!mIsReady) ready();

        return mData;
    }

    /**
     * Should be called when done with packet init
     */
    public void ready() {
        if(mIsReady) return;

        byte[] temp = mMaxBuffer.array();
        // TODO: Varför fungerar inte detta
        mData = mMaxBuffer.array(); //new byte[mMaxBuffer.position()];

        for (int i = 0; i < mData.length; i++) {
            mData[i] = temp[i];
        }

        mIsReady = true;
    }

    // Populating the packet methods
    public void putByte(byte b) {
        PACKET_SIZE += (Byte.SIZE / BYTE);
        mMaxBuffer.put(b);
    }

    public void putInt(int i) {
        PACKET_SIZE += (Integer.SIZE / BYTE);
        mMaxBuffer.putInt(i);
    }

    public void putChar(char c) {
        PACKET_SIZE += (Character.SIZE / BYTE);
        mMaxBuffer.putChar(c);
    }

    public void putDouble(double d) {
        PACKET_SIZE += (Double.SIZE / BYTE);
        mMaxBuffer.putDouble(d);
    }

    public void putFloat(float f) {
        PACKET_SIZE += (Float.SIZE / BYTE);
        mMaxBuffer.putFloat(f);
    }

    public void putShort(short s) {
        PACKET_SIZE += (Short.SIZE / BYTE);
        mMaxBuffer.putShort(s);
    }

    public void putLong(long l) {
        PACKET_SIZE += (Long.SIZE / BYTE);
        mMaxBuffer.putLong(l);
    }


    // Getting the packet values
    public byte getByte() {
        return mBuffer.get();
    }

    public int getInt() {
        return mBuffer.getInt();
    }

    public char getChar() {
        return mBuffer.getChar();
    }

    public double getDouble() {
        return mBuffer.getDouble();
    }

    public float getFloat() {
        return mBuffer.getFloat();
    }

    public short getShort() {
        return mBuffer.getShort();
    }

    public long getLong() {
        return mBuffer.getLong();
    }
}
