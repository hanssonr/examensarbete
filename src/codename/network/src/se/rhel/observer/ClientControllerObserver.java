package se.rhel.observer;

import se.rhel.client.ClientController;
import se.rhel.packet.Packet;

import java.util.ArrayList;

/**
 * Created by Emil on 2014-03-08.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public class ClientControllerObserver implements ClientControllerListener, IObserver{

    private ArrayList<ClientControllerListener> mClientControllerListeners = new ArrayList<>();

    public void addListener(ClientControllerListener toAdd) {
        mClientControllerListeners.add(toAdd);
    }

    @Override
    public void sendTCP(Packet packet) {
        for (ClientControllerListener listener : mClientControllerListeners) {
            listener.sendTCP(packet);
        }
    }

    @Override
    public void sendUDP(Packet packet) {
        for (ClientControllerListener listener : mClientControllerListeners) {
            listener.sendUDP(packet);
        }
    }
}
