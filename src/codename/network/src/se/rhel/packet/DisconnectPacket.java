package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public class DisconnectPacket extends Packet {

    protected DisconnectPacket() {
        super(1, 10);
    }
}
