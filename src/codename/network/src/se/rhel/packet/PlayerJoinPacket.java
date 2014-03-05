package se.rhel.packet;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class PlayerJoinPacket extends Packet {

    public PlayerJoinPacket(float x, float y, float z) {
        super(3, Float.SIZE * 5);

        mBuffer.putFloat(x);
        mBuffer.putFloat(y);
        mBuffer.putFloat(z);
    }
}
