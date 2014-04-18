package se.rhel.network.event;

import se.rhel.packet.Packet;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-03-28.
 */
public interface NetworkListener {
    public void networkEvent(Packet packet);
}
