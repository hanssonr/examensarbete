package se.rhel.observer;

import se.rhel.Connection;
import se.rhel.packet.Packet;

import java.util.ArrayList;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public class Observer implements Listener {
    private ArrayList<Listener> mListeners = new ArrayList<>();

    public void addListener(Listener toAdd) {
        mListeners.add(toAdd);
    }

    @Override
    public void connected(Connection con) {
        for (Listener listener : mListeners) {
            listener.connected(con);
        }
    }

    @Override
    public void disconnected(Connection con) {
        for (Listener listener : mListeners) {
            listener.disconnected(con);
        }
    }

    @Override
    public void received(Connection con, Packet packet) {
        for (Listener listener : mListeners) {
            listener.received(con, packet);
        }
    }
}
