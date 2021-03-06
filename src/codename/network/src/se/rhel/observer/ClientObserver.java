package se.rhel.observer;

import se.rhel.packet.Packet;

import java.util.ArrayList;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public class ClientObserver implements ClientListener, IObserver {

    private ArrayList<ClientListener> mClientListeners = new ArrayList<>();

    public void addListener(ClientListener toAdd) {
        mClientListeners.add(toAdd);
    }

    @Override
    public void connected() {
        for (ClientListener clientListener : mClientListeners) {
            clientListener.connected();
        }
    }

    @Override
    public void disconnected() {
        for (ClientListener clientListener : mClientListeners) {
            clientListener.disconnected();
        }
    }

    @Override
    public void received(Object obj) {
        for (ClientListener clientListener : mClientListeners) {
            clientListener.received(obj);
        }
    }
}
