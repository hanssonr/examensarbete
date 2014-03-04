package se.rhel;

import se.rhel.packet.Packet;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class TcpConnection implements Runnable {

    private Socket mSocket;

    public TcpConnection(Socket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("TCPAddress: " + mSocket.getInetAddress() + " TCPPort: " + mSocket.getPort() + " TCPLocalPort: " + mSocket.getLocalPort());
            DataInputStream dis = new DataInputStream(mSocket.getInputStream());
            byte[] msg = new byte[10];
            dis.readFully(msg);
            parsePacket(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePacket(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);
        Packet.PacketType type = Packet.lookupPacket(buf.get());

        Packet packet = null;
        switch(type) {
            case INVALID:
                break;
            case CONNECT:
                System.out.println("CONNECTION PACKET RECEIVED");
                break;
            case DISCONNECT:
                break;
            case MOVE:
                break;
            default:
                break;
        }
    }


    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
