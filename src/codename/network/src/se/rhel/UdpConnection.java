package se.rhel;

import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.Packet;
import se.rhel.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by rkh on 2014-03-07.
 */
public class UdpConnection implements Runnable {

    private Thread mUpdateThread;
    private boolean mShouldRun;

    private DatagramSocket mSocket;
    private DatagramPacket mPacket;
    private BasePacketHandler mPacketHandler;

    private boolean serverInitialization;

    public UdpConnection(BasePacketHandler handler)  {
        mPacketHandler = handler;
    }

    /**
     * Connects UdpConnection to send data to address
     * @param address
     * @param udpPort
     */
    public void connect(InetAddress address, int udpPort) {
        try {
            mSocket = new DatagramSocket();
            mSocket.connect(address, udpPort);

        } catch (SocketException e) {
            e.printStackTrace();
        }

        start();
        serverInitialization = false;
    }

    /**
     * Binds UdpConnection to listen port
     * @param udpPort
     */
    public void bind(int udpPort) {
        try {
            mSocket = new DatagramSocket(udpPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        start();
        serverInitialization = true;
    }

    private void start() {
        mShouldRun = true;
        mUpdateThread = new Thread(this, "UdpConnectionThread");
        mUpdateThread.start();
    }

    public void run() {
        while(mShouldRun) {
            byte[] buf = new byte[256];

            try {
                // Recieve UDP request
                mPacket = new DatagramPacket(buf, buf.length);
                mSocket.receive(mPacket);

                mPacketHandler.handlePacket(mPacket.getData());

            } catch (IOException e) {
                // Swallow the exception and just close the socket
                stop();
            }
        }
    }

    public boolean isOpen() {
        return mSocket.isClosed() == true ? false : true;
    }

    public void stop() {
        if(!mShouldRun) return;

        Log.info("UdpConnection", "Closing UpdConnection");

        if(!mSocket.isClosed()) {
            mSocket.close();
        } else {
            Log.error("UpdConnection", "Socket already closed");
        }

        mShouldRun = false;
    }

    /**
     * Sends UDP, used by Client
     * @param data
     */
    public void sendUdpFromClient(byte[] data) {
        if(serverInitialization) { return; }
        if(!isOpen()) return;

        try {
            mSocket.send(new DatagramPacket(data, data.length, mSocket.getInetAddress(), mSocket.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends UDP, used by Server
     * @param data
     * @param connection
     */
    public void sendUdpFromServer(byte[] data, Connection connection) {
        if(!serverInitialization) { return; }
        if(!isOpen()) return;

        try {
            mSocket.send(new DatagramPacket(data, data.length, connection.getAddress(), connection.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return mSocket.getLocalPort();
    }
}
