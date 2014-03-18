package se.rhel.client;

import se.rhel.Client;
import se.rhel.UdpConnection;
import se.rhel.observer.ClientObserver;
import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.ConnectionDetailPacket;
import se.rhel.packet.HandshakeResponsePacket;
import se.rhel.packet.LatencyPacket;
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
    public synchronized void handlePacket(byte[] data) {
        super.handlePacket(data);

        if (mObj instanceof HandshakeResponsePacket) {
            HandshakeResponsePacket pkt = new HandshakeResponsePacket(data);
            Log.debug("ClientPacketHandler", mObj.getClass() + " ID: " + pkt.mId);
            mClient.setId(pkt.mId);
            UdpConnection c = mClient.getUDPConnection();

            // Telling listeners if they wants to do something
            ((ClientObserver) mObserver).connected();

            // Also, since the connection been accepted, we can start telling the server
            // that we're still alive, hopefully
            mClient.sendIdlePackage(true);

            // Tell server what udp port to send on
            mClient.sendTcp(new ConnectionDetailPacket(mClient.getId(), c.getLocalPort()));
        }
        else if (mObj instanceof LatencyPacket) {

            mClient.setEndLatency();
            Log.debug("ClientPacketHandler", "Latency: " + mClient.getLatency() + " ms");

        }
        else {
            ((ClientObserver)mObserver).received(mObj, data);
        }
    }
}
