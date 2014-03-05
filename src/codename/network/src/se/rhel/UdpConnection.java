package se.rhel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class UdpConnection extends AConnection {

    private DatagramSocket mUDPSocket;

    public UdpConnection(DatagramSocket socket) {
        mUDPSocket = socket;
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
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Debug > Received on server: " + received);
        System.out.println("UDPAddress: " + packet.getAddress() + " Socket Adress: " + packet.getSocketAddress() + " UDPPort: " + packet.getPort());


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
