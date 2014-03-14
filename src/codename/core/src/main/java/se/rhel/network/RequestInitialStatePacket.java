package se.rhel.network;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class RequestInitialStatePacket extends Packet {

    public int mPlayerId;

    public RequestInitialStatePacket() {}

    public RequestInitialStatePacket(int id) {
        super(RequestInitialStatePacket.class);
        super.putInt(id);
        super.ready();
    }

    public RequestInitialStatePacket(byte[] data) {
        super(data);
        mPlayerId = super.getInt();
    }

}
