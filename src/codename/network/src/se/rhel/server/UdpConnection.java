package se.rhel.server;

import se.rhel.AConnection;
import se.rhel.Connection;
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
    private Server mServer;

    public UdpConnection(DatagramSocket socket, ServerPacketHandler handler, Server server) {
        mUDPSocket = socket;
        mHandler = handler;
        mServer = server;
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
        Packet.PacketType type = Packet.lookupPacket(buf.get());

        switch(type) {
            case IDLE_PACKET:
                int id = buf.getInt();
                for(Connection d : mServer.getConnections()) {
                    System.out.println(">   UdpConnection: Active connections: " + d.getId() + " Last package: " + d.getTimeLastPackage());
                }

                Connection fromConnection = mServer.getConnection(id);
                fromConnection.packageReceived();

                System.out.println(">   UdpConnection: Idle packet recieved from clientId: " + id);
                break;
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop () {
        mUDPSocket.close();
    }


}
