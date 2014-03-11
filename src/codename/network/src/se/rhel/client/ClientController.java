package se.rhel.client;

import se.rhel.observer.ClientControllerListener;
import se.rhel.observer.ClientControllerObserver;
import se.rhel.packet.Packet;

/**
 * Created by Emil on 2014-03-08.
 * assigned to libgdx-gradle-template in se.rhel.client
 */
public abstract class ClientController {
    private ClientControllerObserver mObserver;

    public ClientController() {
        mObserver = new ClientControllerObserver();
    }

    public void addListener(ClientControllerListener toAdd) {
        mObserver.addListener(toAdd);
    }

    public void send(Packet packet) {
        send(packet, true);
    }

    public void send(Packet packet, boolean tcp) {
        if(tcp) {
            mObserver.sendTCP(packet);
        } else {
            mObserver.sendUDP(packet);
        }
    }
}
