package se.rhel.packet;

/**
 * 0    = (byte) packet id
 * 1-4  = (int) player id
 */
public class IdlePacket extends Packet {

    public int mPlayerId;

    public IdlePacket(int id) {
        super(IdlePacket.class);
        super.putInt(id);
        super.ready();
    }

    public IdlePacket(byte[] data) {
        super(data);
        mPlayerId = super.getInt();
    }
}
