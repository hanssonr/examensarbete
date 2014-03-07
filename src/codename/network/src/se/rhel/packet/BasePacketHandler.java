package se.rhel.packet;

import se.rhel.observer.IObserver;
import se.rhel.observer.ServerObserver;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public abstract class BasePacketHandler implements IPacketHandler {

    protected ByteBuffer mBuf;
    protected Packet.PacketType mPacketType;
    protected IObserver mObserver;

    public void handlePacket(byte[] data) {
        mBuf = ByteBuffer.wrap(data);
        mPacketType = Packet.lookupPacket(mBuf.get());

        switch(mPacketType) {
            case DISCONNECT:
                System.out.println(">   BasePacketHandler: Recieved packet DISCONNECT");
                break;
            default:
                break;

        }
    }

    public void setObserver(IObserver obs) {
        mObserver = obs;
    }

}
