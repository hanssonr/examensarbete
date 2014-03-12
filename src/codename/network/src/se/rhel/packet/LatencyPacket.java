package se.rhel.packet;

/**
 * Created by Emil on 2014-03-10.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class LatencyPacket extends Packet {
    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;
    public int mPlayerId;

    public LatencyPacket() {
    }

    public LatencyPacket(int id) {
        super(LatencyPacket.class, PACKET_SIZE);
        mBuffer.putInt(id);
    }

    public LatencyPacket(byte[] data) {
        super(data);
        mPlayerId = mBuffer.getInt();
    }
}
