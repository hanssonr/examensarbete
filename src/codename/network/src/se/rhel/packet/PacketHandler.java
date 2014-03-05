package se.rhel.packet;

import se.rhel.IPacketHandler;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public class PacketHandler implements IPacketHandler {

    @Override
    public void handlePacket(byte[] data) {

        ByteBuffer buf = ByteBuffer.wrap(data);
        Packet.PacketType type = Packet.lookupPacket(buf.get());

        Packet packet = null;

        switch(type) {
            case CONNECT_ACCEPT:
                System.out.println("CONNECTION ACCEPTED WITH ID: " + buf.getInt());
                break;
            case DISCONNECT:
                System.out.println("DISCONNECT");
                break;
            default:
                System.out.println("DEFAULT PACKAGE");
                break;
        }
    }
}
