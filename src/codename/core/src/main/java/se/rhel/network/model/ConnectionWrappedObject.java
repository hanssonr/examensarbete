package se.rhel.network.model;

import se.rhel.Connection;

public class ConnectionWrappedObject {

    private Connection mConnection;
    private Object mObj;

    public ConnectionWrappedObject(Connection con, Object obj) {
        mConnection = con;
        mObj = obj;
    }

    public Connection getConnection() {
        return mConnection;
    }

    public Object getObject() {
        return mObj;
    }
}
