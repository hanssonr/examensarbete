package se.rhel.packet;

/**
 * Created by Emil on 2014-03-10.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class LatencyPacket extends Packet {
    public int mPlayerId;

    public LatencyPacket(int id) {
        super(LatencyPacket.class);
        super.putInt(id);
        super.ready();
    }

    public LatencyPacket(byte[] data) {
        super(data);
        mPlayerId = super.getInt();
    }
}
