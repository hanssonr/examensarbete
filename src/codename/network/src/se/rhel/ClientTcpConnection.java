package se.rhel;

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
    private IPacketHandler mPacketHandler;
    private boolean mIsRunning;

    public ClientTcpConnection(InetAddress address, int port, IPacketHandler handler) throws IOException {
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
                System.out.println("data = " + data);
                mPacketHandler.handlePacket(data);

            } catch (Exception e) {
               // e.printStackTrace();
            }
        }
    }

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

    public void sendTcp(Packet packet) throws IOException {
        DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
        output.write(packet.getData());
    }
}
