package se.rhel.packet;

/**
 * 0    = (byte) packet id
 */
public class ConnectAcceptPacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE;

    public ConnectAcceptPacket(int id) {
        super(PacketType.CONNECT_ACCEPT, PACKET_SIZE);
    }
}
