package se.rhel.event;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-28.
 */
public interface NetworkListener {
    public void networkEvent(Packet packet);
}
