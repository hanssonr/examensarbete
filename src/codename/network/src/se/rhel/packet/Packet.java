package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public abstract class Packet {
    protected ByteBuffer mBuffer;
    protected byte[] mData;

    private byte mPacketId;

    public Packet(int packetId, int packetSize) {
        mPacketId = (byte) packetId;
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
        for(PacketType type : PacketType.values()) {
            if(type.getId() == id) return type;
        }

        return null;
    }

    //PacketType
    public static enum PacketType {
        INVALID(-2), HANDSHAKE_RESPONSE(-1), CONNECT(0), CONNECT_ACCEPT(1), DISCONNECT(2), PLAYER_JOIN(3), REQUEST_INITIAL_STATE(4), IDLE_PACKET(5);

        private int mPacketId;

        private PacketType(int packetId) {
            this.mPacketId = packetId;
        }

        public int getId() {
            return mPacketId;
        }
    }
}
