package se.rhel.network.event;

import se.rhel.event.GameEvent;
import se.rhel.packet.Packet;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-03-28.
 */
public class NetworkEvent implements GameEvent<NetworkListener> {

    Packet packet;

    public NetworkEvent(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void notify(NetworkListener listener) {
        listener.networkEvent(this.packet);
    }
}
