package se.rhel.packet;

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
        System.out.println(">   PacketHandler: type = " + type);
        switch(type) {
            case CONNECT_ACCEPT:
                System.out.println(">   PacketHandler: CONNECTION ACCEPTED WITH ID: " + buf.getInt());
                break;
            case DISCONNECT:
                System.out.println(">   PacketHandler: DISCONNECT");
                break;
            default:
                System.out.println(">   PacketHandler: DEFAULT PACKAGE");
                break;
        }
    }
}
