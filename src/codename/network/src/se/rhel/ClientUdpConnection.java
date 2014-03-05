package se.rhel;

import se.rhel.packet.Packet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.NotActiveException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by rkh on 2014-03-05.
 */
public class ClientUdpConnection implements Runnable {

    private DatagramSocket mUdpSocket;
    private DatagramPacket mUdpPacket;

    private IPacketHandler mPacketHandler;

    public ClientUdpConnection(InetAddress address, int port, IPacketHandler handler) throws SocketException {
        mUdpSocket = new DatagramSocket();
        mUdpSocket.connect(address, port);
        mPacketHandler = handler;

        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true) {
            byte[] buf = new byte[256];

            try {
                // Recieve UDP request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                mUdpSocket.receive(packet);

                mPacketHandler.handlePacket(packet.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendUdp(Packet packet) throws IOException {
        sendUdp(packet.getData());
    }

    public void sendUdp(byte[] data) throws IOException {
        mUdpPacket = new DatagramPacket(data, data.length, mUdpSocket.getInetAddress(), mUdpSocket.getLocalPort());
        mUdpSocket.send(mUdpPacket);
    }

    public int getUdpPort() {
        return mUdpSocket.getLocalPort();
    }
}
