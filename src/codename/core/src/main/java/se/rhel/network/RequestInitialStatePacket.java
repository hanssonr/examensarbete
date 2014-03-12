package se.rhel.network;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class RequestInitialStatePacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;
    public int mPlayerId;

    public RequestInitialStatePacket() {}

    public RequestInitialStatePacket(int id) {
        super(RequestInitialStatePacket.class, PACKET_SIZE);
        mBuffer.putInt(id);
    }

    public RequestInitialStatePacket(byte[] data) {
        super(data);
        mPlayerId = mBuffer.getInt();
    }

}
