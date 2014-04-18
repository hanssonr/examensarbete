package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 *
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.packet
 */
public class RequestInitialStatePacket extends AClientPacket {

    public RequestInitialStatePacket() {}

    public RequestInitialStatePacket(int clientId) {
        super(clientId, RequestInitialStatePacket.class);
        super.ready();
    }

    public RequestInitialStatePacket(byte[] data) {
        super(data);
    }

}
