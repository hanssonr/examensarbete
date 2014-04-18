package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 *
 * 0    = (byte) packet id
 * 1-4  = (float) x position
 * 5-8  = (float) y position
 * 9-12 = (float) z position
 */
public class PlayerPacket extends AClientPacket {

    public Vector3 mPosition;

    public PlayerPacket() {}

    public PlayerPacket(int clientId, Vector3 position) {
        super(clientId, PlayerPacket.class);
        super.putFloat(position.x);
        super.putFloat(position.y);
        super.putFloat(position.z);
        super.ready();
    }

    public PlayerPacket(byte[] data) {
        super(data);
        mPosition = new Vector3(super.getFloat(), super.getFloat(), super.getFloat());
    }
}
