package se.rhel.server;

import se.rhel.Utils;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.ConnectAcceptPacket;

/**
 * Created by rkh on 2014-03-05.
 */
public class ServerPacketHandler extends BasePacketHandler {

    Server mServer;

    public ServerPacketHandler(Server server) {
        mServer = server;
    }

    @Override
    public void handlePacket(byte[] data) {
        super.handlePacket(data);

        switch(mPacketType) {
            case CONNECT:
                //mServer.sendToAllUDP(new ConnectAcceptPacket(Utils.getInstance().generateUniqueId()));
                System.out.println("CONNECT");
                break;
            default:
                //System.out.println("DEFAULT PACKAGE");
                break;
        }
    }

}
