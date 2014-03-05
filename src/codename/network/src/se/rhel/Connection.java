package se.rhel;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Connection {

    private InetAddress mAddress;
    private int mUDPPort;
    private TcpConnection mTcpConnection;
    private boolean mIsConnected;
    private long mLastPackageTime;

    private final int mId;

    public Connection(InetAddress address, int port, TcpConnection tcpConnection) {
        mAddress = address;
        mUDPPort = port;
        mTcpConnection = tcpConnection;
        mId = Utils.getInstance().generateUniqueId();
        mIsConnected = true;
        mLastPackageTime = System.currentTimeMillis();
    }

    public void packageReceived() {
        mLastPackageTime = System.currentTimeMillis();
    }

    public long getTimeLastPackage() {
        return mLastPackageTime;
    }

    public void setConnected(boolean val) {
        mIsConnected = false;
    }

    public void setDisconnected() {
        mTcpConnection.stop();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (mUDPPort != that.mUDPPort) return false;
        if (!mAddress.equals(that.mAddress)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mAddress.hashCode();
        result = 31 * result + mUDPPort;
        return result;
    }

    public boolean isConnected() { return mIsConnected; }
    public Socket getSocket() { return mTcpConnection.getSocket(); }
    public int getPort() {
        return mUDPPort;
    }
    public InetAddress getAddress() {
        return mAddress;
    }
    public int getId() {
        return mId;
    }
}
