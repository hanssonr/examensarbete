package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends AClientPacket {

    public float pX, pY, pZ;
    public float rX, rY, rZ, rW;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int clientId, float px, float py, float pz, float rx, float ry, float rz, float rw) {
        super(clientId, PlayerMovePacket.class);
        putFloat(px);
        putFloat(py);
        putFloat(pz);
        putFloat(rx);
        putFloat(ry);
        putFloat(rz);
        putFloat(rw);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        pX = getFloat();
        pY = getFloat();
        pZ = getFloat();

        rX = getFloat();
        rY = getFloat();
        rZ = getFloat();
        rW = getFloat();
    }
}
