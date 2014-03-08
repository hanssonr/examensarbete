package se.rhel.packet;

/**
 * Created by rkh on 2014-03-04.
 */
public class ConnectAcceptPacket extends Packet {

    public ConnectAcceptPacket(int id) {
        super(PacketType.CONNECT_ACCEPT, 5);
        mBuffer.putInt(id);
    }
}
