package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.AClientPacket;

/**
 * Created by Emil on 2014-04-01.
 */
public class GrenadeCreatePacket extends AClientPacket {

    public Vector3 direction;
    public Vector3 position;

    public GrenadeCreatePacket() {}

    public GrenadeCreatePacket(int clientId, Vector3 position, Vector3 direction) {
        super(clientId, GrenadeCreatePacket.class);

        putFloat(position.x);
        putFloat(position.y);
        putFloat(position.z);

        putFloat(direction.x);
        putFloat(direction.y);
        putFloat(direction.z);

        super.ready();
    }

    public GrenadeCreatePacket(byte[] data) {
        super(data);

        position = new Vector3(getFloat(), getFloat(), getFloat());
        direction = new Vector3(getFloat(), getFloat(), getFloat());
    }
}
