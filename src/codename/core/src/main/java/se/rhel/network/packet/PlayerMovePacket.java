package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends AClientPacket {

    public float pX, pY, pZ;
    public float rY, rW;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int clientId, float px, float py, float pz, float ry, float rw) {
        super(clientId, PlayerMovePacket.class);
        putFloat(px);
        putFloat(py);
        putFloat(pz);
        putFloat(ry);
        putFloat(rw);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        pX = getFloat();
        pY = getFloat();
        pZ = getFloat();
        rY = getFloat();
        rW = getFloat();
    }
}
