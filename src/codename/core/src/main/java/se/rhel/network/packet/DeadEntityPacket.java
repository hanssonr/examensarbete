package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-03-25.
 */
public class DeadEntityPacket extends AClientPacket {

    public DeadEntityPacket(int clientId) {
        super(clientId, DeadEntityPacket.class);
        ready();
    }

    public DeadEntityPacket(byte[] data) {
        super(data);
    }
}
