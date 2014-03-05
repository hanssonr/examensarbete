package se.rhel;

import se.rhel.packet.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Client {

    private DatagramSocket mUdpSocket;
    private DatagramPacket mUdpPacket;

    private Socket mTcpSocket;

    private ClientTcpConnection mTcpConnection;
    private ClientUdpConnection mUdpConnection;

    private IPacketHandler mHandler;

    public Client(IPacketHandler handler) {
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

    public void sendUdp(Packet packet) throws IOException {
        mUdpConnection.sendUdp(packet);
    }
}