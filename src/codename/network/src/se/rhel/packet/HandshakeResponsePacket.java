package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-07.
 */
public class HandshakeResponsePacket extends Packet {

    public int mId;

    public HandshakeResponsePacket(int id) {
        super(-1, 5);

        mBuffer.putInt(id);
    }

    public HandshakeResponsePacket(byte[] data) {
        super(-1, 5);

        mBuffer = ByteBuffer.wrap(data);
        mBuffer.get(); //type
        mId = mBuffer.getInt();
    }
}
