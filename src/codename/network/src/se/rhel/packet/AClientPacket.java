package se.rhel.packet;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-18.
 */
public abstract class AClientPacket extends Packet {

    public int clientId = -1;

    public AClientPacket() {}

    public AClientPacket(int clientId, Class classType) {
        super(classType);
        putInt(clientId);
    }

    public AClientPacket(byte[] data) {
        super(data);
        clientId = getInt();
    }

}