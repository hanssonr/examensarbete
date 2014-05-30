package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import se.rhel.packet.Packet;

/**
 * Created by rkh on 2014-05-30.
 */
public class RespawnPacket extends Packet {

    public int mRespawnId;
    public Vector3 mRespawnPosition;

    public RespawnPacket(int id, Vector3 pos) {
        super(RespawnPacket.class);

        putInt(id);
        putFloat(pos.x);
        putFloat(pos.y);
        putFloat(pos.z);
    }

    public RespawnPacket(byte[] data) {
        super(data);

        mRespawnId = getInt();
        mRespawnPosition = new Vector3(getFloat(), getFloat(), getFloat());
    }
}
