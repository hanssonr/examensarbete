package se.rhel.network;

import se.rhel.packet.Packet;

/**
 * 0    = (byte) packet id
 * 1-4  = (float) x position
 * 5-8  = (float) y position
 * 9-12 = (float) z position
 */
public class PlayerPacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Float.SIZE * 3;
    public float x;
    public float y;
    public float z;

    public PlayerPacket() {}

    public PlayerPacket(float x, float y, float z) {
        super(PlayerPacket.class, PACKET_SIZE);

        mBuffer.putFloat(x);
        mBuffer.putFloat(y);
        mBuffer.putFloat(z);
    }

    public PlayerPacket(byte[] data) {
        super(data);

        x = mBuffer.getFloat();
        y = mBuffer.getFloat();
        z = mBuffer.getFloat();
    }
}
