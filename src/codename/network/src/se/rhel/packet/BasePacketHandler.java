package se.rhel.packet;

import se.rhel.observer.IObserver;
import se.rhel.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public abstract class BasePacketHandler {

    protected ByteBuffer mBuf;
    protected Packet.PacketType mPacketType;
    protected IObserver mObserver;

    public void handlePacket(byte[] data) {
        mBuf = ByteBuffer.wrap(data);
        mPacketType = Packet.lookupPacket(mBuf.get());

        switch(mPacketType) {
            case DISCONNECT:
                Log.debug("BasePacketHandler", "Received packet DISCONNECT");
                break;
        }
    }

    public void setObserver(IObserver obs) {
        mObserver = obs;
    }

}
