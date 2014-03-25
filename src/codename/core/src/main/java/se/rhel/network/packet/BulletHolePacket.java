package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-25.
 */
public class BulletHolePacket extends Packet {

    public Vector3 hitWorld, hitNormal;

    public BulletHolePacket() {}

    public BulletHolePacket(Vector3 hWorld, Vector3 hNormal) {
        super(BulletHolePacket.class);

        putFloat(hWorld.x);
        putFloat(hWorld.y);
        putFloat(hWorld.z);

        putFloat(hNormal.x);
        putFloat(hNormal.y);
        putFloat(hNormal.z);

        ready();
    }

    public BulletHolePacket(byte[] data) {
        super(data);

        hitWorld = new Vector3(getFloat(), getFloat(), getFloat());
        hitNormal = new Vector3(getFloat(), getFloat(), getFloat());
    }
}
