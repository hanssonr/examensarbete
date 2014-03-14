package se.rhel.server;

import se.rhel.TcpConnection;
import se.rhel.UdpConnection;
import se.rhel.observer.ServerListener;
import se.rhel.observer.ServerObserver;
import se.rhel.Connection;
import se.rhel.EndPoint;
import se.rhel.packet.DisconnectPacket;
import se.rhel.packet.Packet;
import se.rhel.packet.PacketRegisterInitializer;
import se.rhel.util.Log;

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
    private static final long SERVER_UPDATE_INTERVAL = 250;

    private Thread SERVER_THREAD;
    private boolean mIsStarted;

    // List with active connections
    private volatile List<Connection> mConnections;

    private UdpConnection mUdpConnection;
    private TcpListener mTCPListener;

    // ServerObserver
    private ServerObserver mServerObserver;

    // Handler
    private ServerPacketHandler mServerPacketHandler;


    public Server() {
        // Log level
        Log.set(Log.LEVEL_DEBUG);
        mConnections = new ArrayList<>();

        mServerObserver = new ServerObserver();
        mServerPacketHandler = new ServerPacketHandler(this);
        mServerPacketHandler.setObserver(mServerObserver);

        PacketRegisterInitializer.register();
    }

    @Override
    public void run() {
        Log.info("Server", "Server started...");
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
        checkAlive();
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
                next.stop();
                iterator.remove();
            } else {
                long timePassed = System.currentTimeMillis() - next.getTimeLastPackage();

                // If there's been a certain amount of time since we heard from the client
                if(timePassed > TIMEOUT_TIME) {
                    //next.setConnected(false);
                    Log.trace("Server", ">   Server: Time passed: " + timePassed + " Timeouttime: " + TIMEOUT_TIME + " Connection: " + next.getId() + " disconnected");

                    // Send packet that you are about to be disconnected / have been
                    // just for convinience for the client since we are going to disconnect anyways
                    //sendTCP(new DisconnectPacket(), next);
                }
            }
        }
    }

    @Override
    public void start() {
        // Starting the server
        mIsStarted = true;
        SERVER_THREAD = new Thread(this, "ServerThread");
        SERVER_THREAD.start();
    }

    public Connection createConnection(TcpConnection tcpConnection) {
        Connection con = new Connection();
        con.initialize(tcpConnection, mUdpConnection);
        return con;
    }

    @Override
    public void stop() {
        if(!mIsStarted)
            return;

        mIsStarted = false;

        SERVER_THREAD.interrupt();
        mUdpConnection.stop();
        mTCPListener.stop();
    }

    /*
    Adds a connection object to the list of current connections
     */
    public boolean addConnection(Connection con) {
        Log.debug("Server", "Should add connection with id " + con.getId());
        if(mConnections.contains(con))
            return false;
        if(mConnections.size() >= MAX_CONNECTIONS)
            return false;

        // Adding a unique ID to the new Connection
        mConnections.add(con);
        Log.debug("Server", "Connection size: " + mConnections.size());

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
     */
    public void sendUDP(Packet packet, Connection conn) {
        Log.trace("Server", "Send UDP > " + Packet.class.getName());
        conn.sendUdp(packet.getData(), conn);
    }

    public void sendTCP(Packet packet, Connection conn) {
        Log.trace("Server", "Send TCP > " + Packet.class.getName());
        conn.sendTcp(packet.getData());
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
            }
        }
    }

    public synchronized Connection getConnection(int id) {
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

    public ServerObserver getObserver() {
        return mServerObserver;
    }

    public void bind(int tcpPort, int udpPort) {
        // Start the TCP ServerListener
        try {
            mTCPListener = new TcpListener(new ServerSocket(tcpPort), this, mServerPacketHandler);
            mTCPListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //setup DatagramSocket and bind listening port
        mUdpConnection = new UdpConnection(mServerPacketHandler);
        mUdpConnection.bind(udpPort);
    }
}