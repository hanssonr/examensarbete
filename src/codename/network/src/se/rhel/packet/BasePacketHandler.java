package se.rhel.packet;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public abstract class BasePacketHandler implements IPacketHandler {

    protected ByteBuffer mBuf;
    protected Packet.PacketType mPacketType;

    public void handlePacket(byte[] data) {
        mBuf = ByteBuffer.wrap(data);
        mPacketType = Packet.lookupPacket(mBuf.get());

        switch(mPacketType) {
            case DISCONNECT:
                System.out.println("DISCONNECT");
                break;
            default:
                break;

        }
    };

}
