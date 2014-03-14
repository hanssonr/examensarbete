package se.rhel.packet;

import se.rhel.observer.IObserver;
import se.rhel.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public abstract class BasePacketHandler {

    protected Class mClassType;
    protected IObserver mObserver;
    protected Object mObj;
    protected Packet mUnknownPacket;

    public void handlePacket(byte[] data) {

        mUnknownPacket = new Packet(data) {
            @Override
            public byte getPacketId() {
                return super.getPacketId();
            }
        };

        mClassType = PacketManager.getInstance().getPacketType(mUnknownPacket.getPacketId());
        if(mClassType == null) {
            System.out.println();
        }

        try {
            mObj = mClassType.newInstance();

        } catch (InstantiationException e) {
            Log.error("BasePacketHandler", "No default constructor found in " + mClassType);
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setObserver(IObserver obs) {
        mObserver = obs;
    }

}
