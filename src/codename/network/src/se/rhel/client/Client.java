package se.rhel.client;

import se.rhel.EndPoint;
import se.rhel.TcpConnection;
import se.rhel.UdpConnection;
import se.rhel.observer.ClientListener;
import se.rhel.observer.ClientObserver;
import se.rhel.packet.*;
import se.rhel.util.Log;

import java.net.*;

public class Client implements EndPoint {

    private static final int CLIENT_UPDATE_INTERVAL = 2000;

    private UdpConnection mUdpConnection;
    private TcpConnection mTcpConnection;
    private ClientObserver mClientObserver;

    private int mId;
    private boolean mIsRunning;
    private boolean mSendIdlePackage;

    public Client() {
        ClientPacketHandler mHandler = new ClientPacketHandler(this);
        mClientObserver = new ClientObserver();
        mHandler.setObserver(mClientObserver);

        mTcpConnection = new TcpConnection(mHandler);
        mUdpConnection = new UdpConnection(mHandler);
    }

    @Override
    public void run() {
        // System.out.println(">   Client: Client started");
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
            sendUdp(new IdlePacket(mId));
        }
    }

    public void sendIdlePackage(boolean value) {
        mSendIdlePackage = value;
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

    public void connect(String host, int tcpPort, int udpPort) {
        try {
            InetAddress address = InetAddress.getByName(host);
            mTcpConnection.connect(address, tcpPort);
            mUdpConnection.connect(address, udpPort);
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
     * @return
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
        System.out.println(">   Client: Send TCP > " + Packet.lookupPacket(data[0]));
        mTcpConnection.sendTcp(data);
    }

    public void sendUdp(Packet packet) {
        sendUdp(packet.getData());
    }

    public void sendUdp(byte[] data) {
        System.out.println(">   Client: Send UDP > " + Packet.lookupPacket(data[0]));
        mUdpConnection.sendUdp(data);
    }

}