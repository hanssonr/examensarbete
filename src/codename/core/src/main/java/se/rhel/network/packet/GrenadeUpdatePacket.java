package se.rhel.network.packet;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.AClientPacket;

/**
 * Created by Emil on 2014-04-28.
 */
public class GrenadeUpdatePacket extends AClientPacket {

    public Vector3 position;
    public Quaternion rotation;
    public boolean isAlive;

    public GrenadeUpdatePacket() {}

    public GrenadeUpdatePacket(int grenadeId, Vector3 position, Quaternion rotation, boolean isAlive) {
        super(grenadeId, GrenadeUpdatePacket.class);
        putFloat(position.x);
        putFloat(position.y);
        putFloat(position.z);

        putFloat(rotation.x);
        putFloat(rotation.y);
        putFloat(rotation.z);
        putFloat(rotation.w);

        putBoolean(isAlive);

        super.ready();
    }

    public GrenadeUpdatePacket(byte[] data) {
        super(data);
        position = new Vector3(getFloat(), getFloat(), getFloat());
        rotation = new Quaternion(getFloat(), getFloat(), getFloat(), getFloat());
        isAlive = getBoolean();
    }
}
