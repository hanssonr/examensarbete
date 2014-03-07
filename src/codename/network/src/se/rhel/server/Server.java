package se.rhel.server;

import se.rhel.observer.ServerListener;
import se.rhel.observer.ServerObserver;
import se.rhel.Connection;
import se.rhel.EndPoint;
import se.rhel.packet.DisconnectPacket;
import se.rhel.packet.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Server implements EndPoint {

    private static final int MAX_CONNECTIONS = 16;
    private static final long TIMEOUT_TIME = 2500;
    private static final long SERVER_UPDATE_INTERVAL = 25;

    public static String NAME;
    private static int PORT;
    private Thread SERVER_THREAD;

    private DatagramSocket mUDPSocket;

    private boolean mIsStarted;

    // List with active connections
    private volatile List<Connection> mConnections;

    private UdpConnection mUDPConnection;
    private TcpListener mTCPListener;

    // ServerObserver
    private ServerObserver mServerObserver;

    // Handler
    private ServerPacketHandler mServerPacketHandler;

    public Server(String name, int port) throws SocketException {
        PORT = port;
        NAME = name;

        mConnections = new ArrayList<>();

        mServerObserver = new ServerObserver();
        mServerPacketHandler = new ServerPacketHandler();
        mServerPacketHandler.setObserver(mServerObserver);
    }

    @Override
    public void run() {
        System.out.println("Debug > Server started..");
        while(mIsStarted) {

            // Do server stuff..
            long startTime = System.currentTimeMillis();
            update();
            long elapsedTime = System.currentTimeMillis() - startTime;

            if(elapsedTime < SERVER_UPDATE_INTERVAL) try {
                Thread.sleep(SERVER_UPDATE_INTERVAL - elapsedTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public long last;
    public void update() {

        if(last != 0) {
            long per = System.currentTimeMillis() - last;
            // System.out.println("Update interval: " + per);
        }
        last = System.currentTimeMillis();

        // Check if any client been dced
        // checkAlive();
    }

    /**
     * Checks the aliveness of current
     * connections and disconnects them
     * if no activity
     */
    private void checkAlive() {
        if(mConnections.size() == 0)
            return;

        for (Iterator<Connection> iterator = mConnections.iterator(); iterator.hasNext(); ) {
            Connection next = iterator.next();

            // Check for dead connections and reamove them
            if(!next.isConnected()) {
                next.setDisconnected();
                iterator.remove();
            } else {
                long timePassed = System.currentTimeMillis() - next.getTimeLastPackage();

                // If there's been a certain amount of time since we heard from the client
                if(timePassed > TIMEOUT_TIME) {
                    next.setConnected(false);
                    System.out.println("Time passed: " + timePassed + " Timeouttime: " + TIMEOUT_TIME + " Connection: " + next.getId() + " disconnected");

                    // Send packet that you are about to be disconnected / have been
                    // just for convinience for the client since we are going to disconnect anyways
                    sendTCP(new DisconnectPacket(), next);
                }
            }
        }
    }

    @Override
    public void start() {

        ServerSocket tcpSocket;

        try {
            mUDPSocket = new DatagramSocket(PORT);
            tcpSocket = new ServerSocket(PORT);

            // Start the TCP ServerListener
            mTCPListener = new TcpListener(tcpSocket, this, mServerPacketHandler);
            mTCPListener.start();

            // Start the udp connection
            mUDPConnection = new UdpConnection(mUDPSocket, mServerPacketHandler);
            mUDPConnection.start();

            // Starting the server
            mIsStarted = true;
            SERVER_THREAD = new Thread(this);
            SERVER_THREAD.start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Server not started");
        }
    }

    @Override
    public void stop() {
        if(!mIsStarted)
            return;

        mIsStarted = false;

        SERVER_THREAD.interrupt();
        mUDPConnection.stop();
        mTCPListener.stop();
    }

    /*
    Adds a connection object to the list of current connections
     */
    public boolean addConnection(Connection con) {
        if(mConnections.contains(con))
            return false;
        if(mConnections.size() >= MAX_CONNECTIONS)
            return false;

        // Adding a unique ID to the new Connection
        mConnections.add(con);

        // Telling whoever is listening
        mServerObserver.connected(con);

        return true;
    }

    public boolean removeConnection(Connection con) {
        if(mConnections.contains(con)) {
            return mConnections.remove(con);
        }
        return false;
    }

    /*
    Send to all Connections through UDP
     */
    public void sendToAllUDP(Packet packet) {
        for (Connection connection : mConnections) {
            sendUDP(packet, connection);
        }
    }

    /**
     * Send to a specific connection through UDP
     * @param packet
     * @param conn
     * @throws IOException
     */
    public void sendUDP(Packet packet, Connection conn) {
        DatagramPacket mUdpPacket = new DatagramPacket(packet.getData(), packet.getData().length, conn.getAddress(), conn.getPort());

        try {
            mUDPSocket.send(mUdpPacket);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void sendTCP(Packet packet, Connection conn) {
        try {
            DataOutputStream output = new DataOutputStream(conn.getSocket().getOutputStream());
            output.write(packet.getData());
            output.flush();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendToAllTCP(Packet packet) {
        for (Connection connection : mConnections) {
            sendTCP(packet, connection);
        }
    }

    public void sendToAllTCPExcept(Packet packet, Connection except) {
        for(Connection con : mConnections) {
            if(!con.equals(except)) {
                sendTCP(packet, con);
            } else {
                //System.out.println("Skipped this one");
            }
        }
    }

    public Connection getConnection(int id) {
        for (Connection connection : mConnections) {
            if(connection.getId() == id)
                return connection;
        }
        return null;
    }

    public List<Connection> getConnections() {
        return mConnections;
    }

    public void addListener(ServerListener toAdd) {
        mServerObserver.addListener(toAdd);
    }

    /**
     * @deprecated use {@link}
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

}