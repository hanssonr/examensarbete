package se.rhel.client;

import se.rhel.EndPoint;
import se.rhel.observer.ClientListener;
import se.rhel.observer.ClientObserver;
import se.rhel.observer.ServerListener;
import se.rhel.observer.ServerObserver;
import se.rhel.packet.*;

import java.io.IOException;
import java.net.*;

public class Client implements EndPoint {

    private static final int CLIENT_UPDATE_INTERVAL = 25;

    private ClientTcpConnection mTcpConnection;
    private ClientUdpConnection mUdpConnection;

    private BasePacketHandler mHandler;

    private ClientObserver mClientObserver;

    private int mId;
    private boolean mIsRunning;
    private boolean mSendIdlePackage;

    public Client() {
        mId = -1;
        mHandler = new ClientPacketHandler(this);
        mClientObserver = new ClientObserver();
        mHandler.setObserver(mClientObserver);
    }

    @Override
    public void run() {
        System.out.println(">   Client: Client started");
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

    private void update () {
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
        new Thread(this).start();
    }

    @Override
    public void stop() {
        if(!mIsRunning)
            return;
        mIsRunning = false;
    }

    public void connect(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        mUdpConnection = new ClientUdpConnection(address, port, mHandler);
        mTcpConnection = new ClientTcpConnection(address, port, mHandler);

        //Send connection request
        sendTcp(new ConnectPacket(mUdpConnection.getUdpPort()));
    }

    public void addListener(ClientListener toAdd) {
        mClientObserver.addListener(toAdd);
    }

    public void sendTcp(Packet packet) {
        try {
            mTcpConnection.sendTcp(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void sendTcp(byte[] data) throws IOException {
        mTcpConnection.sendTcp(data);
    }

    public void sendUdp(Packet packet) {
        try {
            mUdpConnection.sendUdp(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUdp(byte[] data) throws IOException {
        mUdpConnection.sendUdp(data);
    }

}