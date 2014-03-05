package se.rhel.observer;

import se.rhel.Connection;
import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel
 */
public interface ServerListener {
    public void connected(Connection con);
    public void disconnected(Connection con);
    public void received(Connection con, Packet packet);
}
