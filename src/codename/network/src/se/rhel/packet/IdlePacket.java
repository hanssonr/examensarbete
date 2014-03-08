package se.rhel.packet;

/**
 * Created by Emil on 2014-03-07.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class IdlePacket extends Packet {

    public IdlePacket(int id) {
        super(PacketType.IDLE_PACKET, 5);
        mBuffer.putInt(id);
    }

}
