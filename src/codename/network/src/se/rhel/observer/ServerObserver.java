package se.rhel.observer;

import se.rhel.Connection;
import se.rhel.packet.Packet;

import java.util.ArrayList;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public class ServerObserver implements ServerListener, IObserver {
    private ArrayList<ServerListener> mServerListeners = new ArrayList<>();

    public void addListener(ServerListener toAdd) {
        mServerListeners.add(toAdd);
    }

    // For debugging
    public int nrOfListeners() {
        return mServerListeners.size();
    }

    @Override
    public void connected(Connection con) {
        for (ServerListener serverListener : mServerListeners) {
            serverListener.connected(con);
        }
    }

    @Override
    public void disconnected(Connection con) {
        for (ServerListener serverListener : mServerListeners) {
            serverListener.disconnected(con);
        }
    }

    @Override
    public void received(Connection con, Object obj) {
        for (ServerListener serverListener : mServerListeners) {
            serverListener.received(con, obj);
        }
    }
}
