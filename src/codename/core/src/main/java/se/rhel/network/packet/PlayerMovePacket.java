package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.AClientPacket;

/**
 * Group: Network
 *
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends AClientPacket {

    public Vector3 mPosition = new Vector3();
    public float mRotX, mRotY;

    public PlayerMovePacket() {}

    public PlayerMovePacket(int clientId, Vector3 position, float rotX, float rotY) {
        super(clientId, PlayerMovePacket.class);
        putFloat(position.x);
        putFloat(position.y);
        putFloat(position.z);
        putFloat(rotX);
        putFloat(rotY);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        mPosition.x = getFloat();
        mPosition.y = getFloat();
        mPosition.z = getFloat();
        mRotX = getFloat();
        mRotY = getFloat();
    }
}
