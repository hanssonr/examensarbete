package se.rhel.network.packet;

import se.rhel.packet.Packet;

/**
 * Group: Network
 *
 * 0    = (byte) packet id
 * 1-4  = (float) x position
 * 5-8  = (float) y position
 * 9-12 = (float) z position
 */
public class PlayerPacket extends Packet {

    public int clientId;
    public float x;
    public float y;
    public float z;

    public PlayerPacket() {}

    public PlayerPacket(int clientId, float x, float y, float z) {
        super(PlayerPacket.class);
        super.putInt(clientId);
        super.putFloat(x);
        super.putFloat(y);
        super.putFloat(z);
        super.ready();
    }

    public PlayerPacket(byte[] data) {
        super(data);
        clientId = super.getInt();
        x = super.getFloat();
        y = super.getFloat();
        z = super.getFloat();
    }
}
