package se.rhel.client;

import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by rkh on 2014-03-05.
 */
public class ClientTcpConnection implements Runnable {

    private Socket mSocket;
    private BasePacketHandler mPacketHandler;
    private boolean mIsRunning;

    public ClientTcpConnection(InetAddress address, int port, BasePacketHandler handler) throws IOException {
        mSocket = new Socket(address, port);
        mPacketHandler = handler;
        mIsRunning = true;

        new Thread(this).start();
    }

    @Override
    public void run() {
        while(mIsRunning) {
            try {
                DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                byte[] data = new byte[10];
                dis.readFully(data);

                mPacketHandler.handlePacket(data);

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public void sendTcp(Packet packet) throws IOException {
        sendTcp(packet.getData());
    }

    public void sendTcp(byte[] data) throws IOException {
        DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
        output.write(data);
    }

    public void stop() throws IOException {
        mIsRunning = false;
        mSocket.close();
    }
}
