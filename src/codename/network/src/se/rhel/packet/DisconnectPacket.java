package se.rhel.packet;

/**
 * 0    = (byte) packet id
 */
public class DisconnectPacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE;

    public DisconnectPacket() {
        super(PacketType.DISCONNECT, PACKET_SIZE);
    }
}
