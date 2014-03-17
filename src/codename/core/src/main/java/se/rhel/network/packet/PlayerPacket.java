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

    public float x;
    public float y;
    public float z;

    public PlayerPacket() {}

    public PlayerPacket(float x, float y, float z) {
        super(PlayerPacket.class);
        super.putFloat(x);
        super.putFloat(y);
        super.putFloat(z);
        super.ready();
    }

    public PlayerPacket(byte[] data) {
        super(data);

        x = super.getFloat();
        y = super.getFloat();
        z = super.getFloat();
    }
}
