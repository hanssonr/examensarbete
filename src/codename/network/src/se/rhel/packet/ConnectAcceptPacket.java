package se.rhel.packet;

/**
 * 0    = (byte) packet id
 */
public class ConnectAcceptPacket extends Packet {

    public ConnectAcceptPacket(int id) {
        super(ConnectAcceptPacket.class);
        super.ready();
    }
}
