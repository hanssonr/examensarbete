package se.rhel.server;

import se.rhel.AConnection;
import se.rhel.Connection;
import se.rhel.packet.ConnectAcceptPacket;
import se.rhel.packet.ConnectPacket;
import se.rhel.packet.Packet;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class TcpConnection extends AConnection {

    private final Server mServer;
    private Socket mSocket;
    private boolean mIsRunning;

    public TcpConnection(Socket socket, Server server) {
        mServer = server;
        mSocket = socket;
    }

    @Override
    public void run() {
        while(mIsRunning) {
            try {
                // System.out.println("TCPAddress: " + mSocket.getInetAddress() + " TCPPort: " + mSocket.getPort() + " TCPLocalPort: " + mSocket.getLocalPort());
                DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                byte[] msg = new byte[10];
                dis.readFully(msg);
                parseTCPPacket(msg);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    void parseTCPPacket(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        Packet.PacketType type = Packet.lookupPacket(buf.get());

        Packet packet = null;
        switch(type) {
            case INVALID:
                break;
            case CONNECT:
                //System.out.println("CONNECTION PACKET RECEIVED " + type.getId() + " UDP PORT " + buf.getInt());
                Connection con = new Connection(mSocket.getInetAddress(), buf.getInt(), this);

                if(mServer.addConnection(con)) {
                    mServer.sendTCP(new ConnectAcceptPacket(con.getId()), con);
                }

                break;
            default:
                break;
        }
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

        try {
            mSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}