package se.rhel.client;

import se.rhel.observer.ClientObserver;
import se.rhel.packet.BasePacketHandler;

/**
 * Created by rkh on 2014-03-05.
 */
public class ClientPacketHandler extends BasePacketHandler {

    private Client mClient;

    // TODO: Should not be aware of client, really.
    public ClientPacketHandler(Client c) {
        mClient = c;
    }

    @Override
    public void handlePacket(byte[] data) {
        super.handlePacket(data);

        switch(mPacketType) {
            case CONNECT_ACCEPT:
                int id = mBuf.getInt();
                // Setting the ID on client
                mClient.setId(id);
                System.out.println("CONNECTION ACCEPTED WITH ID: " + id);
                break;

            case PLAYER_JOIN:
                System.out.println("Player joined LOL!");
                ((ClientObserver)mObserver).connected();
                break;

            default:
                System.out.println("DEFAULT PACKAGE: " + " TYPE: " + mPacketType + ", DATA: " + data);
                break;
        }
    }
}
