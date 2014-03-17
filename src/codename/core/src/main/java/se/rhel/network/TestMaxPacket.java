package se.rhel.network;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-14.
 */
public class TestMaxPacket extends Packet {

    public TestMaxPacket() {}

    public TestMaxPacket(int id) {
        super(TestMaxPacket.class);
        int max = 511;

        for (int i = 0; i < max; i++) {
            putByte(Byte.valueOf("1"));
        }
        ready();
    }

    public TestMaxPacket(byte[] data) {

    }
}
