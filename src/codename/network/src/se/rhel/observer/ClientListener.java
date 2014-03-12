package se.rhel.observer;

import se.rhel.Connection;
import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public interface ClientListener {
    public void connected();
    public void disconnected();
    public void received(Packet packet);
    public void received(Object obj);
}
