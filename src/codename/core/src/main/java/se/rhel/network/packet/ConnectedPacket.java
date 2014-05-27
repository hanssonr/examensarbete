package se.rhel.network.packet;

import se.rhel.packet.Packet;

/**
 * Created by rkh on 2014-05-27.
 */

public class ConnectedPacket extends Packet {

    public ConnectedPacket() {
        super(ConnectedPacket.class);
    }

    public ConnectedPacket(byte[] data) {
        super(data);
    }
}
