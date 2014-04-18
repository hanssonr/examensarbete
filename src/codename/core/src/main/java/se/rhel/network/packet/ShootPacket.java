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

    public Vector3 vFrom, vTo, vFrom2, vTo2;

    public ShootPacket() {}

    public ShootPacket(int clientId, Vector3 from, Vector3 to, Vector3 vFrom, Vector3 vTo, Vector3 vFrom2, Vector3 vTo2) {
        super(clientId, ShootPacket.class);
        putFloat(from.x);
        putFloat(from.y);
        putFloat(from.z);

        putFloat(to.x);
        putFloat(to.y);
        putFloat(to.z);

        putFloat(vFrom.x);
        putFloat(vFrom.y);
        putFloat(vFrom.z);

        putFloat(vTo.x);
        putFloat(vTo.y);
        putFloat(vTo.z);

        putFloat(vFrom2.x);
        putFloat(vFrom2.y);
        putFloat(vFrom2.z);

        putFloat(vTo2.x);
        putFloat(vTo2.y);
        putFloat(vTo2.z);

        super.ready();
    }

    public ShootPacket(byte[] data) {
        super(data);

        mFrom = new Vector3(getFloat(), getFloat(), getFloat());
        mTo = new Vector3(getFloat(), getFloat(), getFloat());

        vFrom = new Vector3(getFloat(), getFloat(), getFloat());
        vTo = new Vector3(getFloat(), getFloat(), getFloat());
        vFrom2 = new Vector3(getFloat(), getFloat(), getFloat());
        vTo2 = new Vector3(getFloat(), getFloat(), getFloat());
    }

}
