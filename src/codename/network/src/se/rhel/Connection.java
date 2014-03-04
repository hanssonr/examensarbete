package se.rhel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Connection {

    private InetAddress mAddress;
    private int mPort;
    private TcpConnection mTcpConnection;

    private final int mId;

    public Connection(InetAddress address, int port, TcpConnection tcpConnection) {
        mAddress = address;
        mPort = port;
        mTcpConnection = tcpConnection;
        mId = Utils.getInstance().generateUniqueId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (mPort != that.mPort) return false;
        if (!mAddress.equals(that.mAddress)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mAddress.hashCode();
        result = 31 * result + mPort;
        return result;
    }

    public int getPort() {
        return mPort;
    }

    public InetAddress getAddress() {
        return mAddress;
    }

    public int getId() {
        return mId;
    }
}
