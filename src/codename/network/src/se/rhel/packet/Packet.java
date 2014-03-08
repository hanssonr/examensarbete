package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public abstract class Packet {
    protected ByteBuffer mBuffer;
    protected byte[] mData;
    protected byte mPacketId;

    public Packet(PacketType type, int packetSize) {
        mPacketId = PacketUtils.getInstance().getPacketId(type);

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
        INVALID, HANDSHAKE_RESPONSE, CONNECT, CONNECT_ACCEPT, DISCONNECT, PLAYER_JOIN, REQUEST_INITIAL_STATE, IDLE_PACKET
    }
}
