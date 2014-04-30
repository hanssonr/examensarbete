package se.rhel.packet;

import se.rhel.observer.IObserver;
import se.rhel.util.Log;

import java.lang.reflect.InvocationTargetException;
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

        if(data[0] == -118) {
            System.out.println();
        }

        if(data[0] == 0) {
            throw new IllegalArgumentException("Packet missing id");
        }

        mUnknownPacket = new Packet(data) {
            @Override
            public byte getPacketId() {
                return super.getPacketId();
            }
        };

        mClassType = PacketManager.getInstance().getPacketType(mUnknownPacket.getPacketId());

        try {
            //mObj = mClassType.newInstance();
            mObj = mClassType.getConstructor(byte[].class).newInstance(data);

        } catch (InstantiationException e) {
            Log.error("BasePacketHandler", "No default constructor found in " + mClassType);
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setObserver(IObserver obs) {
        mObserver = obs;
    }

}
