package se.rhel.packet;

/**
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class RequestInitialStatePacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;
    public int mPlayerId;

    public RequestInitialStatePacket(int id) {
        super(PacketType.REQUEST_INITIAL_STATE, PACKET_SIZE);
        mBuffer.putInt(id);
    }

    public RequestInitialStatePacket(byte[] data) {
        super(data);
        mPlayerId = mBuffer.getInt();
    }

}
