package se.rhel.server;

import se.rhel.Connection;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.RequestInitialStatePacket;

/**
 * Created by rkh on 2014-03-05.
 */
public class ServerPacketHandler extends BasePacketHandler {

    Server mServer;

    public ServerPacketHandler(Server server) {
        mServer = server;
    }

    @Override
    public void handlePacket(byte[] data)  {
        super.handlePacket(data);
        int id = -1;
        Connection fromConnection;

        switch(mPacketType) {
            case CONNECT:
                //mServer.sendToAllUDP(new ConnectAcceptPacket(Utils.getInstance().generateUniqueId()));
                System.out.println("CONNECT");
                break;
            case REQUEST_INITIAL_STATE:
                // This right here should be the default from every packet
                id = mBuf.getInt();
                fromConnection = mServer.getConnection(id);

                // We really dont care about what packet has been sent, just tell
                // the listeners about it
                System.out.println(">   ServerPacketHandler: Number of active listeners: " + mServer.getObserver().nrOfListeners());
                mServer.getObserver().received(fromConnection, new RequestInitialStatePacket(id));
                break;
            case IDLE_PACKET:
                id = mBuf.getInt();
                // System.out.println(">   ServerPacketHandler: Idle Packet, Id: " + id);
//                for(Connection d : mServer.getConnections()) {
//                    System.out.println(">   ServerPacketHandler: Active connections: " + d.getId() + " Last package: " + d.getTimeLastPackage());
//                }

                if(id != 1) {
                    fromConnection = mServer.getConnection(id);
                    fromConnection.packageReceived();
                } else {
                    fromConnection = mServer.getConnection(id);
                    fromConnection.packageReceived();
                }


                // System.out.println(">   ServerPacketHandler: Idle packet recieved from clientId: " + id);
                break;
            default:
                //System.out.println("DEFAULT PACKAGE");
                break;
        }
    }
}

