package se.rhel.packet;

import se.rhel.observer.IObserver;
import se.rhel.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public abstract class BasePacketHandler {

    protected ByteBuffer mBuf;
    private Class mClassType;
    protected IObserver mObserver;
    protected Object mObj;

    public void handlePacket(byte[] data) {
        mBuf = ByteBuffer.wrap(data);
        mClassType = PacketManager.getInstance().getPacketType(mBuf.get());

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
