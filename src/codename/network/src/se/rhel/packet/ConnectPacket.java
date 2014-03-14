package se.rhel.packet;

/**
 * 0    = (byte) packet id
 * 1-4  = (int) player id
 */
public class ConnectPacket extends Packet {

    public int mPlayerId;

    public ConnectPacket(int playerId) {
        super(ConnectPacket.class);
        super.putInt(playerId);
        super.ready();
    }

    public ConnectPacket(byte[] data) {
        super(data);

        mPlayerId = super.getInt();
    }
}
