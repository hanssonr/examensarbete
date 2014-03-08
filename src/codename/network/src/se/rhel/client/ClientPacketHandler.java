package se.rhel.client;

import se.rhel.Connection;
import se.rhel.observer.ClientObserver;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.HandshakeResponsePacket;
import se.rhel.packet.PlayerJoinPacket;
import se.rhel.util.Log;

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
            case PLAYER_JOIN:
                Log.debug("ClientPacketHandler", "Player joined packet!");
                // TODO: This is just fake!
                ((ClientObserver)mObserver).received(new PlayerJoinPacket(0f, 10f, 0f));
                break;

            case HANDSHAKE_RESPONSE:
                HandshakeResponsePacket pkt = new HandshakeResponsePacket(data);
                Log.debug("ClientPacketHandler", mPacketType + " ID: " + pkt.mId);
                mClient.setId(pkt.mId);

                // Telling listeners if they wants to do something
                ((ClientObserver)mObserver).connected();

                // Also, since the connection been accepted, we can start telling the server
                // that we're still alive, hopefully
                mClient.sendIdlePackage(true);
                break;
        }
    }
}
