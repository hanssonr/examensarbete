package se.rhel;

import se.rhel.packet.BasePacketHandler;
import se.rhel.packet.Packet;
import se.rhel.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by rkh on 2014-03-07.
 */
public class TcpConnection implements Runnable {

    private Thread mUpdateThread = null;
    private boolean mShouldRun = false;
    private Socket mSocket;

    private BasePacketHandler mPacketHandler;

    public TcpConnection(BasePacketHandler handler) {
        mPacketHandler = handler;
    }

    public void connect(InetAddress address, int tcpPort) {
        try {
            mSocket = new Socket(address, tcpPort);
            mSocket.setTcpNoDelay(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    public void bindSocket(Socket socket) {
        try {
            mSocket = socket;
            mSocket.setTcpNoDelay(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        start();
    }

    private void start() {
        mShouldRun = true;
        if(mUpdateThread == null) {
            mUpdateThread = new Thread(this, "TcpConnectionThread");
            mUpdateThread.start();
        }
    }

    public void run() {
        while(mShouldRun) {
            try {
                DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                byte[] data = new byte[Snaek.PACKAGE_SIZE];
                dis.read(data);

                mPacketHandler.handlePacket(data);
            } catch (IOException e) {
                // Swallow the exception and just close the socket
                stop();
            }
        }
    }

    /**
     * @return true if the connection is open, otherwise false
     */
    public boolean isOpen() {
       return mSocket.isClosed() == true ? false : true;
    }

    public void sendTcp(byte[] data) {
        Log.debug("TcpConnection", "size: " + data.length);
        if(isOpen()) {
            try {
                DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
                output.write(data);
                output.flush();
            } catch (IOException e) {
                Log.error("TcpConnection", e.getMessage());
            }
        } else {
            Log.error("TcpConnection", "Socket is closed");
        }
    }

    public void stop() {
        if(!mShouldRun) return;

        Log.info("TcpConnection", "Closing TcpConnection");
        try {
            if(!mSocket.isClosed()) {
                mSocket.close();
            } else {
                Log.error("TcpConnection", "Socket is already closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mShouldRun = false;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public InetAddress getInetAddress() {
        return mSocket.getInetAddress();
    }
}
