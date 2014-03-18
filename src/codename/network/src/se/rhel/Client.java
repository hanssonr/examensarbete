package se.rhel;

import se.rhel.EndPoint;
import se.rhel.TcpConnection;
import se.rhel.UdpConnection;
import se.rhel.client.ClientPacketHandler;
import se.rhel.observer.ClientListener;
import se.rhel.observer.ClientObserver;
import se.rhel.packet.*;
import se.rhel.util.Log;

import java.net.*;
import java.util.ArrayList;

public class Client implements EndPoint {

    private static final int CLIENT_UPDATE_INTERVAL = 2000;

    private UdpConnection mUdpConnection;
    private TcpConnection mTcpConnection;
    private ClientObserver mClientObserver;

    private int mId = -1;
    private boolean mIsRunning;
    private boolean mSendIdlePackage;

    private static long startLatancy;
    private static long endLatency;
    private static ArrayList<Long> mAverageLatancey = new ArrayList<>();
    private static long currLatency = -1L;

    Client() {
        ClientPacketHandler mHandler = new ClientPacketHandler(this);
        mClientObserver = new ClientObserver();
        mHandler.setObserver(mClientObserver);

        mTcpConnection = new TcpConnection(mHandler);
        mUdpConnection = new UdpConnection(mHandler);

        PacketRegisterInitializer.register();
    }

    @Override
    public void run() {
        Log.info("Client", "Client started");
        while(mIsRunning) {
            // Do client stuff..
            long startTime = System.currentTimeMillis();
            update();
            long elapsedTime = System.currentTimeMillis() - startTime;

            if(elapsedTime < CLIENT_UPDATE_INTERVAL) try {
                Thread.sleep(CLIENT_UPDATE_INTERVAL - elapsedTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update () {
        // We want to send still-alive-packages to the server
        if(mSendIdlePackage) {
            // Also send latency packet
            sendTcp(new LatencyPacket(mId));
            startLatancy = System.currentTimeMillis();
            sendUdp(new IdlePacket(mId));
        }
    }

    public void sendIdlePackage(boolean value) {
        mSendIdlePackage = value;
    }

    /**
     * Should be called when a time has been measured between
     * send and receive a packet
     */
    public void setEndLatency() {
        endLatency = System.currentTimeMillis();

        if(endLatency != 0L && startLatancy != 0L) {
            mAverageLatancey.add((endLatency-startLatancy));
        }

        if(mAverageLatancey.size() == 10) {
            long sum = 0L;
            for(long latency : mAverageLatancey) {
                sum += latency;
            }
            sum /= mAverageLatancey.size();
            currLatency = sum;
            mAverageLatancey.clear();
        }
    }

    /**
     * Returns current average latency, or -1L if none
     * @return long latency
     */
    public static long getLatency() {
        if(currLatency != -1L)
            return currLatency;
        if(mAverageLatancey.size() > 0)
            return mAverageLatancey.get(0);
        return -1L;
    }

    @Override
    public void start() {
        mIsRunning = true;
        new Thread(this, "ClientThread").start();
    }

    @Override
    public void stop() {
        if(!mIsRunning)
            return;
        mIsRunning = false;
    }

    public UdpConnection getUDPConnection() {
        return mUdpConnection;
    }

    public void connect(String host, int tcpPort, int udpPort) {
        try {
            InetAddress address = InetAddress.getByName(host);
            mUdpConnection.connect(address, udpPort);
            mTcpConnection.connect(address, tcpPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void addListener(ClientListener toAdd) {
        mClientObserver.addListener(toAdd);
    }

    /**
     * If equals -1 it means that
     * the client haven't got response
     * from server yet
     * @return clientId
     */
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void sendTcp(Packet packet) {
        sendTcp(packet.getData());
    }

    public void sendTcp(byte[] data) {
        Log.trace("Client", "Send TCP > " + Packet.class.getName());
        mTcpConnection.sendTcp(data);
    }

    public void sendUdp(Packet packet) {
        sendUdp(packet.getData());
    }

    public void sendUdp(byte[] data) {
        Log.trace("Client", "Send UDP >" + Packet.class.getName());
        mUdpConnection.sendUdpFromClient(data);
    }

}