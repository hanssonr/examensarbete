package se.rhel.network;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends Packet {

    public int clientId;
    public float x, y, z;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int clientId, float x, float y, float z) {
        super(PlayerMovePacket.class);
        putInt(clientId);
        putFloat(x);
        putFloat(y);
        putFloat(z);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        clientId = getInt();
        x = getFloat();
        y = getFloat();
        z = getFloat();
    }
}
