package se.rhel.packet;

/**
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class RequestInitialStatePacket extends Packet {

    public RequestInitialStatePacket(int id) {
        super(PacketType.REQUEST_INITIAL_STATE, 5);
        mBuffer.putInt(id);
    }
}
