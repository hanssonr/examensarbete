package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.component.IComponent;
import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-03-17.
 */
public class PlayerMovePacket extends AClientPacket {

    public Vector3 mPosition = new Vector3();
    public Vector3 mRotation = new Vector3();

    public PlayerMovePacket(int clientId, Vector3 position, Vector3 rotation) {
        super(clientId, PlayerMovePacket.class);
        putFloat(position.x);
        putFloat(position.y);
        putFloat(position.z);
        putFloat(rotation.x);
        putFloat(rotation.y);
        putFloat(rotation.z);
        ready();
    }

    public PlayerMovePacket(byte[] data) {
        super(data);
        mPosition.x = getFloat();
        mPosition.y = getFloat();
        mPosition.z = getFloat();
        mRotation.x = getFloat();
        mRotation.y = getFloat();
        mRotation.z = getFloat();
    }
}
