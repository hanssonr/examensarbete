package se.rhel.client;

import se.rhel.packet.*;

import java.io.IOException;
import java.net.*;

public class Client {

    private ClientTcpConnection mTcpConnection;
    private ClientUdpConnection mUdpConnection;

    private BasePacketHandler mHandler;

    public Client(BasePacketHandler handler) {
        mHandler = handler;
    }

    public void connect(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        mUdpConnection = new ClientUdpConnection(address, port, mHandler);
        mTcpConnection = new ClientTcpConnection(address, port, mHandler);

        //Send connection request
        sendTcp(new ConnectPacket(mUdpConnection.getUdpPort()));
    }

    public void sendTcp(Packet packet) throws IOException {
        mTcpConnection.sendTcp(packet);
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