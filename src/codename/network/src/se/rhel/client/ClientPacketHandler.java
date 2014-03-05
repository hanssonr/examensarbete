package se.rhel.client;

import se.rhel.observer.ClientObserver;
import se.rhel.observer.ServerObserver;
import se.rhel.packet.BasePacketHandler;

/**
 * Created by rkh on 2014-03-05.
 */
public class ClientPacketHandler extends BasePacketHandler {

    protected ClientObserver mClientObserver;

    @Override
    public void handlePacket(byte[] data) {
        super.handlePacket(data);

        switch(mPacketType) {
            case CONNECT_ACCEPT:
                System.out.println("CONNECTION ACCEPTED WITH ID: " + mBuf.getInt());
                break;

            case PLAYER_JOIN:
                System.out.println("Player joined LOL!");
                mClientObserver.connected();
                break;

            default:
                System.out.println("DEFAULT PACKAGE");
                break;
        }
    }

    public void setmClientObserver(ClientObserver toSet) {
        mClientObserver = toSet;
    }


}
