package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * 0    = (byte) id
 * 1-4  = (int) server_id
 */
public class HandshakeResponsePacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;;
    public int mId;

    public HandshakeResponsePacket(int id) {
        super(PacketType.HANDSHAKE_RESPONSE, PACKET_SIZE);

        mBuffer.putInt(id);
    }

    public HandshakeResponsePacket(byte[] data) {
        super(data);

        mId = mBuffer.getInt();
    }
}
