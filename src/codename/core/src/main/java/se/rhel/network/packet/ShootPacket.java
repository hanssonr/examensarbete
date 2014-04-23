package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-03-21.
 */
public class ShootPacket extends AClientPacket {

    public Vector3 mFrom;
    public Vector3 mTo;

    public ShootPacket() {}

    public ShootPacket(int clientId, Vector3 from, Vector3 to) {
        super(clientId, ShootPacket.class);
        putFloat(from.x);
        putFloat(from.y);
        putFloat(from.z);

        putFloat(to.x);
        putFloat(to.y);
        putFloat(to.z);

        super.ready();
    }

    public ShootPacket(byte[] data) {
        super(data);

        mFrom = new Vector3(getFloat(), getFloat(), getFloat());
        mTo = new Vector3(getFloat(), getFloat(), getFloat());
    }

}
