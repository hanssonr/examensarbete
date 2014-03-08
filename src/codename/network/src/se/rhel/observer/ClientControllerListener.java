package se.rhel.observer;

import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-08.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public interface ClientControllerListener {
    public void sendTCP(Packet packet);
    public void sendUDP(Packet packet);
}
