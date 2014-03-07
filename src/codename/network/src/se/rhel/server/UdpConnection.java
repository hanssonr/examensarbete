package se.rhel.server;

import se.rhel.AConnection;
import se.rhel.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class UdpConnection extends AConnection {

    private DatagramSocket mUDPSocket;
    private ServerPacketHandler mHandler;

    public UdpConnection(DatagramSocket socket, ServerPacketHandler handler) {
        mUDPSocket = socket;
        mHandler = handler;
    }

    @Override
    public void run() {
        while(true) {
            byte[] buf = new byte[256];
            try {
                // Recieve UDP request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                mUDPSocket.receive(packet);
                parseUDPPacket(packet);
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }

    /**
     * Takes care of the packet just received on the server
     * @param packet
     * @throws java.io.IOException
     */
    private void parseUDPPacket(DatagramPacket packet) {
        ByteBuffer buf = ByteBuffer.wrap(packet.getData());
        System.out.println("Debug > Received on server: " + Packet.lookupPacket(buf.get()));
        System.out.println("PORT > " + buf.getInt());
        //System.out.println("UDPAddress: " + packet.getAddress() + " Socket Adress: " + packet.getSocketAddress() + " UDPPort: " + packet.getPort());


        // Should we add the connection?
        /*if(addConnection(new Connection(packet.getAddress(), packet.getPort()))) {
            System.out.println("Debug > Connection added");
        } else {
            System.out.println("Debug > Connection already exists");
        }
        System.out.println("Debug > Connections size: " + mConnections.size());

        sendToAllUDP();*/
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop () {
        mUDPSocket.close();
    }


}
