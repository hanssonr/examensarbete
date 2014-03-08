package se.rhel.packet;

/**
 * 0    = (byte) packet id
 * 1-4  = (int) player id
 */
public class IdlePacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;
    public int mPlayerId;

    public IdlePacket(int id) {
        super(PacketType.IDLE_PACKET, PACKET_SIZE);
        mBuffer.putInt(id);
    }

    public IdlePacket(byte[] data) {
        super(data);
        mPlayerId = mBuffer.getInt();
    }

}
