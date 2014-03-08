package se.rhel;

import se.rhel.packet.BasePacketHandler;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Connection {

    private boolean mIsConnected;
    private long mLastPackageTime;
    private final int mId;

    protected UdpConnection mUdpConnection;
    protected TcpConnection mTcpConnection;

    public Connection() {
        mId = Utils.getInstance().generateUniqueId();
    }

    public void packageReceived() {
        mLastPackageTime = System.currentTimeMillis();
    }

    public long getTimeLastPackage() {
        return mLastPackageTime;
    }

    public void setConnected(boolean val) {
        mIsConnected = val;
    }

    public void setDisconnected() {
        mTcpConnection.stop();
    }

    @Override
    public String toString() {
        return "Connection{" +
                "mId=" + mId +
                ", mLastPackageTime=" + mLastPackageTime +
                ", mIsConnected=" + mIsConnected +
                ", mUDPPort=" + mUdpConnection.getPort() +
                ", mAddress=" + mTcpConnection.getInetAddress() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (mUdpConnection.getPort() != that.mUdpConnection.getPort()) return false;
        if (!mTcpConnection.getInetAddress().equals(that.mTcpConnection.getInetAddress())) return false;
        if (mId != that.mId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mTcpConnection.getInetAddress().hashCode();
        result = 31 * result + mUdpConnection.getPort();
        return result;
    }

    public boolean isConnected() { return mIsConnected; }
    public Socket getSocket() { return mTcpConnection.getSocket(); }
    public int getPort() { return mUdpConnection.getPort(); }
    public InetAddress getAddress() { return mTcpConnection.getInetAddress(); }
    public int getId() {
        return mId;
    }

    /**
     * Sets TCP/UDP connection objects
     * @param tcpCon
     * @param udpCon
     */
    public void initialize(TcpConnection tcpCon, UdpConnection udpCon) {
        mTcpConnection = tcpCon;
        mUdpConnection = udpCon;
        mIsConnected = true;
        mLastPackageTime = System.currentTimeMillis();
    }

    public void sendUdp(byte[] data, Connection connection) {
        mUdpConnection.sendUdp(data, connection);
    }

    public void sendTcp(byte[] data) {
        mTcpConnection.sendTcp(data);
    }
}
