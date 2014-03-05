package se.rhel.client;

import se.rhel.packet.BasePacketHandler;

import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public class ClientPacketHandler extends BasePacketHandler {

    @Override
    public void handlePacket(byte[] data) {
        super.handlePacket(data);

        switch(mPacketType) {
            case CONNECT_ACCEPT:
                System.out.println("CONNECTION ACCEPTED WITH ID: " + mBuf.getInt());
                break;

            case PLAYER_JOIN:
                System.out.println("Player joined LOL!");
                break;

            default:
                System.out.println("DEFAULT PACKAGE");
                break;
        }
    }


}
