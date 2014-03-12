package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public abstract class Packet {
    protected ByteBuffer mBuffer;
    protected byte[] mData;
    protected byte mPacketId;

    public Packet() {}

    /**
     * Used when creating packet from input data
     * @param data
     */
    public Packet(byte[] data) {
        mBuffer = ByteBuffer.wrap(data);
        mPacketId = mBuffer.get();
    }


    public Packet(Class<?> classtype, int packetSize) {
        mPacketId = (byte)PacketManager.getInstance().getPacketId(classtype);

        mData = new byte[packetSize];
        mBuffer = ByteBuffer.wrap(mData);
        mBuffer.put(mPacketId);
    }

    public byte getPacketId() {
        return mPacketId;
    }

    public byte[] getData() {
        return mData;
    }

    public static PacketType lookupPacket(byte id){
        return PacketUtils.getInstance().getPacketType(id);
    }

    public static enum PacketType {
        INVALID, HANDSHAKE_RESPONSE,
        CONNECT, CONNECT_ACCEPT,
        DISCONNECT, IDLE_PACKET,
        LATENCY_PACKET
    }
}
