package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public class DisconnectPacket extends Packet {

    public DisconnectPacket() {
        super(PacketType.DISCONNECT, 10);
    }
}
