package se.rhel.client;

import se.rhel.observer.ClientListener;
import se.rhel.observer.ClientObserver;
import se.rhel.observer.ServerListener;
import se.rhel.observer.ServerObserver;
import se.rhel.packet.*;

import java.io.IOException;
import java.net.*;

public class Client {

    private ClientTcpConnection mTcpConnection;
    private ClientUdpConnection mUdpConnection;

    private BasePacketHandler mHandler;

    private ClientObserver mClientObserver;

    private int mId;

    public Client() {
        mId = -1;
        mHandler = new ClientPacketHandler(this);
        mClientObserver = new ClientObserver();
        mHandler.setObserver(mClientObserver);
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

    public void sendUdp(Packet packet) throws IOException {
        mUdpConnection.sendUdp(packet);
    }

    public void sendUdp(byte[] data) throws IOException {
        mUdpConnection.sendUdp(data);
    }
}