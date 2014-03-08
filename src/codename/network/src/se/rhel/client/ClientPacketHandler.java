package se.rhel.client;

import se.rhel.observer.ClientObserver;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.HandshakeResponsePacket;
import se.rhel.packet.PlayerJoinPacket;

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
                System.out.println(">   ClientPacketHandler: Connection accepted >> PLAYER_ID: " + id);

                // Telling listeners if they wants to do something
                ((ClientObserver)mObserver).connected();

                // Also, since the connection been accepted, we can start telling the server
                // that we're still alive, hopefully
                mClient.sendIdlePackage(true);
                break;

            case PLAYER_JOIN:
                System.out.println(">   ClientPacketHandler: Player joined packet!");
                // TODO: This is just fake!
                ((ClientObserver)mObserver).received(new PlayerJoinPacket(0f, 10f, 0f));
                break;

            case HANDSHAKE_RESPONSE:
                HandshakeResponsePacket pkt = new HandshakeResponsePacket(data);
                System.out.println(">   ClientPacketHandler: " + mPacketType + " ID: " + pkt.mId);
                mClient.setId(pkt.mId);
                break;

            default:
                System.out.println(">   ClientPacketHandler: DEFAULT PACKAGE: " + " TYPE: " + mPacketType + ", DATA: " + data);
                break;
        }
    }
}
