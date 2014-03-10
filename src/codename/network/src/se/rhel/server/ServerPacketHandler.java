package se.rhel.server;

import se.rhel.Connection;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.IdlePacket;
import se.rhel.packet.LatencyPacket;
import se.rhel.packet.RequestInitialStatePacket;
import se.rhel.util.Log;

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

        Connection fromConnection;

        switch(mPacketType) {
            case CONNECT:
                break;
            case REQUEST_INITIAL_STATE:
                RequestInitialStatePacket risp = new RequestInitialStatePacket(data);
                fromConnection = mServer.getConnection(risp.mPlayerId);

                // We really dont care about what packet has been sent, just tell
                // the listeners about it
                Log.debug("ServerPacketHandler", "Number of active listeners: " + mServer.getObserver().nrOfListeners());
                mServer.getObserver().received(fromConnection, new RequestInitialStatePacket(risp.mPlayerId));
                break;
            case IDLE_PACKET:
                IdlePacket ip = new IdlePacket(data);

                fromConnection = mServer.getConnection(ip.mPlayerId);
                fromConnection.packageReceived();

                // mServer.sendUDP(new IdlePacket(1), fromConnection);
                break;
            case LATENCY_PACKET:
                // Sending a dummy response, i.e for latency measurement
                LatencyPacket lp = new LatencyPacket(data);
                fromConnection = mServer.getConnection(lp.mPlayerId);
                mServer.sendTCP(new LatencyPacket(0), fromConnection);
                break;
            default:
                break;
        }
    }
}

