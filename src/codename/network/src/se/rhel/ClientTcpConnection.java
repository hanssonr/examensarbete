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

    public ClientTcpConnection(InetAddress address, int port, IPacketHandler handler) throws IOException {
        mSocket = new Socket(address, port);
        mPacketHandler = handler;

        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(mSocket.getInputStream());
            byte[] data = new byte[10];
            dis.readFully(data);

            mPacketHandler.handlePacket(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTcp(Packet packet) throws IOException {
        DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
        output.write(packet.getData());
    }
}
