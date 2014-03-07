package se.rhel.server;

import se.rhel.AConnection;
import se.rhel.Connection;
import se.rhel.packet.*;

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
    private ServerPacketHandler mHandler;
    private boolean mIsRunning;

    public TcpConnection(Socket socket, Server server, ServerPacketHandler handler) throws SocketException {
        mServer = server;
        mSocket = socket;
        mSocket.setTcpNoDelay(true);
        mHandler = handler;
    }

    @Override
    public void run() {
        while(mIsRunning) {
            try {
                // System.out.println("TCPAddress: " + mSocket.getInetAddress() + " TCPPort: " + mSocket.getPort() + " TCPLocalPort: " + mSocket.getLocalPort());
                DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                byte[] msg = new byte[256]; //TODO: se till att storlek inte är mindre än största paketet
                dis.read(msg); //read retunerar storleken men läser också in data, readFully som användes förut läste in alltid in all data(?)
                parseTCPPacket(msg);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    void parseTCPPacket(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        Packet.PacketType type = Packet.lookupPacket(buf.get());
        System.out.println(">   TcpConnection: PARSE " + " Type: " + type + ", Data: " + data);
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
            case REQUEST_INITIAL_STATE:
                // This right here should be the default from every packet
                int id = buf.getInt();
                Connection fromConnection = mServer.getConnection(id);

                // We really dont care about what packet has been sent, just tell
                // the listeners about it
                System.out.println(">   TcpConnection: Number of active listeners: " + mServer.getObserver().nrOfListeners());
                mServer.getObserver().received(fromConnection, new RequestInitialStatePacket(id));
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
