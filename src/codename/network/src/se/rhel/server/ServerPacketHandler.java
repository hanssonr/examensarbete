package se.rhel.server;

import se.rhel.Connection;
import se.rhel.observer.ServerObserver;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.ConnectionDetailPacket;
import se.rhel.packet.IdlePacket;
import se.rhel.packet.LatencyPacket;

/**
 * Created by rkh on 2014-03-05.
 */
public class ServerPacketHandler extends BasePacketHandler {

    Server mServer;

    public ServerPacketHandler(Server server) {
        mServer = server;
    }

    @Override
    public synchronized void handlePacket(byte[] data)  {
        super.handlePacket(data);

        Connection fromConnection;// = mServer.getConnection(mBuf.getInt());

        if (mObj instanceof IdlePacket) {
            IdlePacket ip = new IdlePacket(data);

            fromConnection = mServer.getConnection(ip.mPlayerId);
            fromConnection.packageReceived();
        }
        else if (mObj instanceof LatencyPacket) {
            //Sending a dummy response, i.e for latency measurement
            LatencyPacket lp = new LatencyPacket(data);

            fromConnection = mServer.getConnection(lp.mPlayerId);
            mServer.sendTCP(new LatencyPacket(0), fromConnection);
        }
        else if (mObj instanceof ConnectionDetailPacket) {
            ConnectionDetailPacket cdp = new ConnectionDetailPacket(data);
            Connection c = mServer.getConnection(cdp.mPlayerId);
            c.setUdpPort(cdp.mUdpPort);
        }
        else {
            int id = mUnknownPacket.getInt();
            fromConnection = mServer.getConnection(id);
            ((ServerObserver)mObserver).received(fromConnection, mObj, data);
        }
    }
}

