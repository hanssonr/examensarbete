package se.rhel.packet;

/**
 * 0    = (byte) packet id
 */
public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
        super(DisconnectPacket.class);
        super.ready();
    }
}
