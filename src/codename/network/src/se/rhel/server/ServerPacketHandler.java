package se.rhel.server;

import se.rhel.packet.BasePacketHandler;

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
                System.out.println("CONNECT");
                break;
            default:
                System.out.println("DEFAULT PACKAGE");
                break;
        }
    }

}
