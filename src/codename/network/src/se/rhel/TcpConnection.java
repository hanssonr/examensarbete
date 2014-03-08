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
                byte[] data = new byte[256];
                dis.read(data);

                mPacketHandler.handlePacket(data);

            } catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    public void sendTcp(byte[] data) {
        try {
            DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
            output.write(data);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.debug("TcpConnection", "Send " + Packet.lookupPacket(data[0]));
    }

    public void stop() {
        mShouldRun = false;
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public InetAddress getInetAddress() {
        return mSocket.getInetAddress();
    }
}
