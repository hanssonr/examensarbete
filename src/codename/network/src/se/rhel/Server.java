package se.rhel;

import se.rhel.packet.ConnectAcceptPacket;
import se.rhel.packet.Packet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Server implements EndPoint {

    public static String NAME;
    private static int PORT;

    private DatagramSocket mUDPSocket;
    private ServerSocket mTCPSocket;
    private BufferedReader mIn;

    private boolean mIsStarted;

    // List with active connections
    private List<Connection> mConnections;

    public Server(String name, int port) throws SocketException {
        PORT = port;
        NAME = name;

        mConnections = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("Debug > Server started..");
        while(mIsStarted) {
            try {
                // Waiting for new connections
                TcpConnection c = new TcpConnection(mTCPSocket.accept(), this);
                // Adding the connection
                c.start();
                // addConnection(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {

        try {
            mUDPSocket = new DatagramSocket(PORT);
            mTCPSocket = new ServerSocket(PORT);
        } catch (SocketException e) {
            System.out.println(e.getMessage());
            System.out.println("Server not started");
            return;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Server not started");
            return;
        }

        UdpConnection udpc = new UdpConnection(mUDPSocket);
        udpc.start();

        mIsStarted = true;
        new Thread(this).start();
    }

    @Override
    public void stop() {
        if(!mIsStarted)
            return;

        mIsStarted = false;
        mUDPSocket.close();
    }

    /*
    Adds a connection object to the list of current connections
     */
    public boolean addConnection(Connection con) {
        if(mConnections.contains(con))
            return false;

        // Adding a unique ID to the new Connection
        mConnections.add(con);
        return true;
    }

    /*
    Send to all Connections through UDP
     */
    public void sendToAllUDP() throws IOException {
        for (Connection connection : mConnections) {
            sendToUDP(connection);
        }
    }

    public void sendUdpPacket(Packet packet, Connection con) throws IOException {
        DatagramPacket mUdpPacket = new DatagramPacket(packet.getData(), packet.getData().length, con.getAddress(), con.getPort());
        mUDPSocket.send(mUdpPacket);
    }

    /**
     * Send to a specific connection through UDP
     * @param con
     * @throws IOException
     */
    public void sendToUDP(Connection con) throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket packet;

        // Response
        String responseString = "Jag måste i ärlighetens namn, säga att jag är en ärlig person";
        buffer = responseString.getBytes();

        InetAddress address = con.getAddress();
        int port = con.getPort();

        System.out.println("Server port: " + PORT + " EmilClient port: " + port);

        System.out.println("Debug > Sending on server..");
        packet = new DatagramPacket(buffer, buffer.length, address, port);
        mUDPSocket.send(packet);
    }

    public void sendTCP(Socket mSocket, Packet packet) {
        try {
            DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
            output.write(packet.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}