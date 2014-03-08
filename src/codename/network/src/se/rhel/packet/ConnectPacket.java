package se.rhel.packet;

/**
 * Created by rkh on 2014-03-04.
 */
public class ConnectPacket extends Packet {

    public ConnectPacket(int udpPort) {
        super(PacketType.CONNECT, 5);
        mBuffer.putInt(udpPort);
    }

}
