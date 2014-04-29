package se.rhel.packet;

import org.omg.DynamicAny._DynAnyFactoryStub;

import java.nio.ByteBuffer;

/**
 * 0    = (byte) id
 * 1-4  = (int) server_id
 */
public class HandshakeResponsePacket extends Packet {

    public int mId;

    public HandshakeResponsePacket(int id) {
        super(HandshakeResponsePacket.class);
        super.putInt(id);
        super.ready();
    }

    public HandshakeResponsePacket(byte[] data) {
        super(data);
        mId = super.getInt();
    }
}
