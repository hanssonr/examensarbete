package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-04.
 */
public class ConnectPacket extends Packet {

    public ConnectPacket() {
        super(0, 10);
    }

}
