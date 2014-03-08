package se.rhel.server;

import se.rhel.Connection;
import se.rhel.TcpConnection;
import se.rhel.packet.ConnectAcceptPacket;
import se.rhel.packet.HandshakeResponsePacket;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Emil on 2014-03-05.
 * Waiting for incomming TCP connections
 */
public class TcpListener implements Runnable {

    private ServerSocket mTCPSocket;
    private Server mServer;
    private ServerPacketHandler mHandler;

    public TcpListener(ServerSocket tcpSocket, Server server, ServerPacketHandler handler) {
        mTCPSocket = tcpSocket;
        mServer = server;
        mHandler = handler;
    }

    @Override
    public void run() {
        while(true) {
            try {
                TcpConnection tcpCon = new TcpConnection(mHandler);
                tcpCon.bindSocket(mTCPSocket.accept());
                Connection connection = mServer.createConnection(tcpCon);
                mServer.addConnection(connection);
                mServer.sendTCP(new HandshakeResponsePacket(connection.getId()), connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        try {
            mTCPSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
