package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends AClientPacket {

    public float x, y, z;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int clientId, float x, float y, float z) {
        super(clientId, PlayerMovePacket.class);
        putFloat(x);
        putFloat(y);
        putFloat(z);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        x = getFloat();
        y = getFloat();
        z = getFloat();
    }
}
